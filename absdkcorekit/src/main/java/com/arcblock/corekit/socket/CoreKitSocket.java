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

import android.text.TextUtils;

import com.arcblock.corekit.utils.CoreKitLogUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class CoreKitSocket {

	public static final int RECONNECT_INTERVAL_MS = 5000;
	private static final int DEFAULT_HEARTBEAT_INTERVAL = 7000;
	private static final int MAX_RETRY_NUM = 10;

	private WebSocket webSocket = null;
	private boolean reconnectOnFailure = true;
	private TimerTask reconnectTimerTask = null;
	private final int heartbeatInterval;
	private TimerTask heartbeatTimerTask = null;
	private String endpointUri = null;
	private final Set<ISocketOpenCallback> socketOpenCallbacks = Collections
			.newSetFromMap(new HashMap<ISocketOpenCallback, Boolean>());
	private final Set<ISocketCloseCallback> socketCloseCallbacks = Collections
			.newSetFromMap(new HashMap<ISocketCloseCallback, Boolean>());
	private final Set<IErrorCallback> errorCallbacks = Collections
			.newSetFromMap(new HashMap<IErrorCallback, Boolean>());
	private final LinkedBlockingQueue<RequestBody> sendBuffer = new LinkedBlockingQueue<>();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final List<Channel> channels = new ArrayList<>();
	private final Set<IMessageCallback> messageCallbacks = Collections.newSetFromMap(new HashMap<IMessageCallback, Boolean>());
	private Timer timer = null;
	private final OkHttpClient httpClient;
	private int refNo = 1;
	private boolean isOpening = false;
	private int reTryNum = 0;


	public class CoreKitWSListener extends WebSocketListener {

		@Override
		public void onOpen(WebSocket webSocket, Response response) {
			CoreKitLogUtils.e("WebSocket onOpen: " + webSocket + " thread name=>" + Thread.currentThread().getName());
			CoreKitSocket.this.webSocket = webSocket;
			cancelReconnectTimer();
			startHeartbeatTimer();
			for (final ISocketOpenCallback callback : socketOpenCallbacks) {
				callback.onOpen();
			}
			CoreKitSocket.this.flushSendBuffer();
			isOpening = false;
			reTryNum = 0;
		}

		@Override
		public void onMessage(WebSocket webSocket, String text) {
			isOpening = false;

			CoreKitLogUtils.e("onMessage: " + text + " thread name =>" + Thread.currentThread().getName());
			try {
				final CoreKitMsgBean coreKitMsgBean = objectMapper.readValue(text, CoreKitMsgBean.class);
				synchronized (channels) {
					for (final Channel channel : channels) {
						if (channel.isMember(coreKitMsgBean.getTopic())) {
							channel.trigger(coreKitMsgBean.getEvent(), coreKitMsgBean);
						}
					}
				}

				for (final IMessageCallback callback : messageCallbacks) {
					callback.onMessage(coreKitMsgBean);
				}
			} catch (IOException e) {
				CoreKitLogUtils.e("Failed to read message payload " + e.toString());
			}
		}

		@Override
		public void onMessage(WebSocket webSocket, ByteString bytes) {
			onMessage(webSocket, bytes.toString());
		}

		@Override
		public void onClosing(WebSocket webSocket, int code, String reason) {
			isOpening = false;
		}

		@Override
		public void onClosed(WebSocket webSocket, int code, String reason) {
			isOpening = false;
			CoreKitLogUtils.e("WebSocket onClose " + code + "/" + reason);
			CoreKitSocket.this.webSocket = null;
			for (final ISocketCloseCallback callback : socketCloseCallbacks) {
				callback.onClose();
			}
		}

		@Override
		public void onFailure(WebSocket webSocket, Throwable t, Response response) {
			isOpening = false;
			CoreKitLogUtils.e("WebSocket connection error " + t.toString() + " thread name=>" + Thread.currentThread().getName());
			try {
				//TODO if there are multiple errorCallbacks do we really want to trigger
				//the same channel error callbacks multiple times?
				triggerChannelError();
				for (final IErrorCallback callback : errorCallbacks) {
					callback.onError(t.getMessage());
				}
			} finally {
				// Assume closed on failure
				if (CoreKitSocket.this.webSocket != null) {
					try {
						CoreKitSocket.this.webSocket.close(1001 /*CLOSE_GOING_AWAY*/, "EOF received");
					} finally {
						CoreKitSocket.this.webSocket = null;
					}
				}
				if (reconnectOnFailure) {
					scheduleReconnectTimer();
				}
			}
		}
	}

	private final CoreKitWSListener wsListener = new CoreKitWSListener();

	public CoreKitSocket(final String endpointUri) {
		this(endpointUri, new OkHttpClient(), DEFAULT_HEARTBEAT_INTERVAL);
	}

	public CoreKitSocket(final String endpointUri, final OkHttpClient okHttpClient) {
		this(endpointUri, okHttpClient, DEFAULT_HEARTBEAT_INTERVAL);
	}

	public CoreKitSocket(final String endpointUri, final OkHttpClient okHttpClient, final int heartbeatIntervalInMs) {
		CoreKitLogUtils.e("PhoenixSocket({})" + endpointUri);
		this.endpointUri = endpointUri;
		this.httpClient = okHttpClient;
		this.heartbeatInterval = heartbeatIntervalInMs;
		this.timer = new Timer("Reconnect Timer for " + endpointUri);
	}

	private void cancelHeartbeatTimer() {
		if (CoreKitSocket.this.heartbeatTimerTask != null) {
			CoreKitSocket.this.heartbeatTimerTask.cancel();
		}
	}

	private void cancelReconnectTimer() {
		if (CoreKitSocket.this.reconnectTimerTask != null) {
			CoreKitSocket.this.reconnectTimerTask.cancel();
		}
	}

	private void startHeartbeatTimer() {
		CoreKitSocket.this.heartbeatTimerTask = new TimerTask() {
			@Override
			public void run() {
				CoreKitLogUtils.e("heartbeatTimerTask run");
				if (CoreKitSocket.this.isConnected()) {
					try {
						CoreKitMsgBean msgBean = new CoreKitMsgBean("phoenix", "heartbeat",
								new ObjectNode(JsonNodeFactory.instance), CoreKitSocket.this.makeRef());
						CoreKitSocket.this.push(msgBean);
					} catch (Exception e) {
						CoreKitLogUtils.e("Failed to send heartbeat" + e.toString());
					}
				}
			}
		};
		timer.schedule(CoreKitSocket.this.heartbeatTimerTask, CoreKitSocket.this.heartbeatInterval,
				CoreKitSocket.this.heartbeatInterval);
	}

	/**
	 * @return true if the socket connection is connected
	 */
	public boolean isConnected() {
		return webSocket != null;
	}

	public boolean isOpening() {
		return isOpening;
	}

	public void setOpening(boolean opening) {
		isOpening = opening;
	}

	private void flushSendBuffer() {
		while (this.isConnected() && !this.sendBuffer.isEmpty()) {
			final RequestBody body = this.sendBuffer.remove();
			this.webSocket.send(body.toString());
		}
	}

	private void triggerChannelError() {
		try {
			synchronized (channels) {
				for (final Channel channel : channels) {
					channel.trigger(ChannelEvent.ERROR.getPhxEvent(), null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connect() {
		CoreKitLogUtils.e("do connect" + Thread.currentThread().getName());
		disconnect();
		isOpening = true;
		final Request request = new Request.Builder().url(endpointUri).build();
		webSocket = httpClient.newWebSocket(request, wsListener);
	}

	public void disconnect() {
		CoreKitLogUtils.e("do disconnect");
		if (webSocket != null) {
			webSocket.close(1001 /*CLOSE_GOING_AWAY*/, "Disconnected by client");
		}
		cancelHeartbeatTimer();
		cancelReconnectTimer();
	}

	/**
	 * Sets up and schedules a timer task to make repeated reconnect attempts at configured
	 * intervals
	 */
	private void scheduleReconnectTimer() {
		cancelReconnectTimer();
		cancelHeartbeatTimer();
		if (reTryNum > MAX_RETRY_NUM) {
			CoreKitLogUtils.e("have to max retry limit");
			return;
		}
		CoreKitSocket.this.reconnectTimerTask = new TimerTask() {
			@Override
			public void run() {
				CoreKitLogUtils.e("reconnectTimerTask run");
				try {
					CoreKitSocket.this.connect();
					reTryNum = reTryNum + 1;
				} catch (Exception e) {
					CoreKitLogUtils.e("Failed to reconnect to " + e.toString());
				}
			}
		};
		timer.schedule(CoreKitSocket.this.reconnectTimerTask, RECONNECT_INTERVAL_MS);
	}

	synchronized String makeRef() {
		int val = refNo++;
		if (refNo == Integer.MAX_VALUE) {
			refNo = 0;
		}
		return Integer.toString(val);
	}

	/**
	 * Sends a message msgBean on this socket
	 *
	 * @param msgBean The msgBean
	 * @return This socket instance
	 * @throws IOException Thrown if the message cannot be sent
	 */
	public CoreKitSocket push(final CoreKitMsgBean msgBean) throws IOException {
		final ObjectNode node = objectMapper.createObjectNode();
		node.put("topic", msgBean.getTopic());
		node.put("event", msgBean.getEvent());
		node.put("ref", msgBean.getRef());
		node.set("payload", msgBean.getPayload() == null ? objectMapper.createObjectNode() : msgBean.getPayload());
		final String json = objectMapper.writeValueAsString(node);
		CoreKitLogUtils.e("push: " + msgBean + ", isConnected: " + isConnected() + ", JSON:" + json);
		RequestBody body = RequestBody.create(MediaType.parse("text/xml"), json);
		if (this.isConnected()) {
			webSocket.send(json);
		} else {
			this.sendBuffer.add(body);
		}
		return this;
	}

	/**
	 * Should the socket attempt to reconnect if websocket.onFailure is called.
	 *
	 * @param reconnectOnFailure reconnect value
	 */
	public void reconectOnFailure(final boolean reconnectOnFailure) {
		this.reconnectOnFailure = reconnectOnFailure;
	}

	/**
	 * Removes the specified channel if it is known to the socket
	 *
	 * @param channel The channel to be removed
	 */
	public void remove(final Channel channel) {
		try {
			synchronized (channels) {
				for (final Iterator chanIter = channels.iterator(); chanIter.hasNext(); ) {
					if (chanIter.next() == channel) {
						chanIter.remove();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Register a callback for SocketEvent.ERROR events
	 *
	 * @param callback The callback to receive CLOSE events
	 * @return This Socket instance
	 */
	public CoreKitSocket onClose(final ISocketCloseCallback callback) {
		this.socketCloseCallbacks.add(callback);
		return this;
	}

	/**
	 * Register a callback for SocketEvent.ERROR events
	 *
	 * @param callback The callback to receive ERROR events
	 * @return This Socket instance
	 */
	public CoreKitSocket onError(final IErrorCallback callback) {
		this.errorCallbacks.add(callback);
		return this;
	}

	/**
	 * Register a callback for SocketEvent.MESSAGE events
	 *
	 * @param callback The callback to receive MESSAGE events
	 * @return This Socket instance
	 */
	public CoreKitSocket onMessage(final IMessageCallback callback) {
		this.messageCallbacks.add(callback);
		return this;
	}

	/**
	 * Register a callback for SocketEvent.OPEN events
	 *
	 * @param callback The callback to receive OPEN events
	 * @return This Socket instance
	 */
	public CoreKitSocket onOpen(final ISocketOpenCallback callback) {
		cancelReconnectTimer();
		this.socketOpenCallbacks.add(callback);
		return this;
	}

	/**
	 * Retrieve a channel instance for the specified topic
	 *
	 * @param topic   The channel topic
	 * @param payload The message payload
	 * @return A Channel instance to be used for sending and receiving events for the topic
	 */
	public Channel chan(final String topic, final JsonNode payload) {
		CoreKitLogUtils.e("chan: " + topic + " , " + payload);
		return getChannel(topic, payload);
	}

	/**
	 * get channel with same topic , maybe also need the same payload later
	 *
	 * @param topic
	 * @param payload
	 * @return
	 */
	private Channel getChannel(final String topic, final JsonNode payload) {
		Channel channel = null;
		synchronized (channels) {
			for (int i = 0; i < channels.size(); i++) {
				if (TextUtils.equals(channels.get(i).getTopic(), topic)) {
					return channels.get(i);
				}
			}
			channel = new Channel(topic, payload, this);
			channels.add(channel);
		}
		return channel;
	}

	@Override
	public String toString() {
		return "PhoenixSocket{" +
				"endpointUri='" + endpointUri + '\'' +
				", channels(" + channels.size() + ")=" + channels +
				", refNo=" + refNo +
				", webSocket=" + webSocket +
				'}';
	}

	static String replyEventName(final String ref) {
		return "chan_reply_" + ref;
	}


}
