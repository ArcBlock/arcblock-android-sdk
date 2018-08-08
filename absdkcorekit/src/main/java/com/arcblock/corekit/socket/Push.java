/*
 * Copyright (c) 2017-present ArcBlock Foundation Ltd <https://www.arcblock.io/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.arcblock.corekit.socket;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class Push {

	private static final String TAG = "Push";

	private class TimeoutHook {

		private ITimeoutCallback callback;

		private final long ms;

		private TimerTask timerTask;

		public TimeoutHook(final long ms) {
			this.ms = ms;
		}

		public ITimeoutCallback getCallback() {
			return callback;
		}

		public long getMs() {
			return ms;
		}

		public TimerTask getTimerTask() {
			return timerTask;
		}

		public boolean hasCallback() {
			return this.callback != null;
		}

		public void setCallback(final ITimeoutCallback callback) {
			this.callback = callback;
		}

		public void setTimerTask(final TimerTask timerTask) {
			this.timerTask = timerTask;
		}
	}

	private Channel channel = null;

	private String event = null;

	private JsonNode payload = null;

	private final Map<String, List<IMessageCallback>> recHooks = new HashMap<>();

	private CoreKitMsgBean receivedMsgBean = null;

	private String refEvent = null;

	private boolean sent = false;

	private final TimeoutHook timeoutHook;

	Push(final Channel channel, final String event, final JsonNode payload, final long timeout) {
		this.channel = channel;
		this.event = event;
		this.payload = payload;
		this.timeoutHook = new TimeoutHook(timeout);
	}

	/**
	 * Registers for notifications on status messages
	 *
	 * @param status   The message status to register callbacks on
	 * @param callback The callback handler
	 * @return This instance's self
	 */
	public Push receive(final String status, final IMessageCallback callback) {
		if (this.receivedMsgBean != null) {
			final String receivedStatus = this.receivedMsgBean.getResponseStatus();
			if (receivedStatus != null && receivedStatus.equals(status)) {
				callback.onMessage(this.receivedMsgBean);
			}
		}
		synchronized (recHooks) {
			List<IMessageCallback> statusHooks = this.recHooks.get(status);
			if (statusHooks == null) {
				statusHooks = new ArrayList<>();
				this.recHooks.put(status, statusHooks);
			}
			statusHooks.add(callback);
		}

		return this;
	}

	/**
	 * Registers for notification of message response timeout
	 *
	 * @param callback The callback handler called when timeout is reached
	 * @return This instance's self
	 */
	public Push timeout(final ITimeoutCallback callback) {
		if (this.timeoutHook.hasCallback()) {
			throw new IllegalStateException("Only a single after hook can be applied to a Push");
		}

		this.timeoutHook.setCallback(callback);

		return this;
	}

	Channel getChannel() {
		return channel;
	}

	String getEvent() {
		return event;
	}

	JsonNode getPayload() {
		return payload;
	}

	Map<String, List<IMessageCallback>> getRecHooks() {
		return recHooks;
	}

	CoreKitMsgBean getReceivedMsgBean() {
		return receivedMsgBean;
	}

	boolean isSent() {
		return sent;
	}

	void send() throws IOException {
		final String ref = channel.getSocket().makeRef();
		Log.e(TAG, "Push send, ref=" + ref);

		this.refEvent = CoreKitSocket.replyEventName(ref);
		this.receivedMsgBean = null;

		this.channel.on(this.refEvent, new IMessageCallback() {
			@Override
			public void onMessage(final CoreKitMsgBean msgBean) {
				Push.this.receivedMsgBean = msgBean;
				Push.this.matchReceive(receivedMsgBean.getResponseStatus(), msgBean);
				Push.this.cancelRefEvent();
				Push.this.cancelTimeout();
			}
		});

		this.startTimeout();
		this.sent = true;
		final CoreKitMsgBean msgBean = new CoreKitMsgBean(this.channel.getTopic(), this.event, this.payload, ref);
		this.channel.getSocket().push(msgBean);
	}

	private void cancelRefEvent() {
		this.channel.off(this.refEvent);
	}

	private void cancelTimeout() {
		this.timeoutHook.getTimerTask().cancel();
		this.timeoutHook.setTimerTask(null);
	}

	private TimerTask createTimerTask() {
		final Runnable callback = new Runnable() {
			@Override
			public void run() {
				Push.this.cancelRefEvent();
				if (Push.this.timeoutHook.hasCallback()) {
					Push.this.timeoutHook.getCallback().onTimeout();
				}
			}
		};

		return new TimerTask() {
			@Override
			public void run() {
				callback.run();
			}
		};
	}

	private void matchReceive(final String status, final CoreKitMsgBean envelope) {
		synchronized (recHooks) {
			final List<IMessageCallback> statusCallbacks = this.recHooks.get(status);
			if (statusCallbacks != null) {
				for (final IMessageCallback callback : statusCallbacks) {
					callback.onMessage(envelope);
				}
			}
		}
	}

	private void startTimeout() {
		this.timeoutHook.setTimerTask(createTimerTask());
		this.channel.scheduleTask(this.timeoutHook.getTimerTask(), this.timeoutHook.getMs());
	}
}