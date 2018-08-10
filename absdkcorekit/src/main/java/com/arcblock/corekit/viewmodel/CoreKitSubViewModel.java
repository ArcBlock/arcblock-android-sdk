package com.arcblock.corekit.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.socket.Channel;
import com.arcblock.corekit.socket.ChannelState;
import com.arcblock.corekit.socket.CoreKitMsgBean;
import com.arcblock.corekit.socket.IMessageCallback;
import com.arcblock.corekit.utils.CoreKitLogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class CoreKitSubViewModel<T> extends ViewModel {

	private static final String TOPIC = "__absinthe__:control";
	private static final String EVENT = "subscription:data";

	private ABCoreKitClient mABCoreKitClient;
	private MutableLiveData<CoreKitBean<T>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();
	private Channel channel;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private Class<T> tClass;
	private String subscriptionId;

	public CoreKitSubViewModel(Context context, int apiType, Class<T> tClass) {
		this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context, apiType);
		this.tClass = tClass;
		channel = mABCoreKitClient.getCoreKitSocket().chan(TOPIC, null);
	}

	public CoreKitSubViewModel(ABCoreKitClient aBCoreKitClient, Class<T> tClass) {
		this.mABCoreKitClient = aBCoreKitClient;
		this.tClass = tClass;
		channel = mABCoreKitClient.getCoreKitSocket().chan(TOPIC, null);
	}

	public MutableLiveData<CoreKitBean<T>> subscription(String queryDocument) {
		makeFlow(queryDocument)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<CoreKitBean<T>>() {
					@Override
					public void onSubscribe(Subscription s) {
						s.request(128);
					}

					@Override
					public void onNext(CoreKitBean<T> coreKitBean) {
						mCoreKitBeanMutableLiveData.postValue(coreKitBean);
					}

					@Override
					public void onError(Throwable t) {
						mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(null, CoreKitBean.FAIL_CODE, t.toString()));
					}

					@Override
					public void onComplete() {

					}
				});
		return mCoreKitBeanMutableLiveData;
	}

	private Flowable<CoreKitBean<T>> makeFlow(final String queryDocument) {
		return Flowable.create(new FlowableOnSubscribe<CoreKitBean<T>>() {
			@Override
			public void subscribe(final FlowableEmitter<CoreKitBean<T>> emitter) {
				initChannel(emitter, queryDocument);
			}
		}, BackpressureStrategy.LATEST);
	}

	private void initChannel(final FlowableEmitter<CoreKitBean<T>> emitter, final String queryDocument) {
		try {
			CoreKitLogUtils.e("channel id=>" + channel.toString() + "  this viewmodel id=>" + CoreKitSubViewModel.this.toString());
			if (channel.getState() == ChannelState.CLOSED) {
				// only join when the channel is closed
				channel.join().receive("ok", new IMessageCallback() {
					@Override
					public void onMessage(final CoreKitMsgBean msgBean) {
						CoreKitLogUtils.e("join=>onMessage=>" + msgBean);
						pushDoc(queryDocument, emitter);
					}
				});
			} else {
				// if already join, just push a new doc with different payload
				pushDoc(queryDocument, emitter);
			}

			channel.on(EVENT, new IMessageCallback() {
				@Override
				public void onMessage(final CoreKitMsgBean msgBean) {
					CoreKitLogUtils.e("channel EVENT onMessage thread name =>" + Thread.currentThread().getName());
					try {
						if (!TextUtils.isEmpty(msgBean.getTopic()) && msgBean.getTopic().startsWith("__absinthe__:doc:")) {
							String data = msgBean.getPayload().get("result").get("data").toString();
							String tempSubId = msgBean.getPayload().get("subscriptionId").asText("");
							if (TextUtils.equals(tempSubId, subscriptionId)) {
								T temp = new Gson().fromJson(data, tClass);
								if (!emitter.isCancelled()) {
									emitter.onNext(new CoreKitBean(temp, CoreKitBean.SUCCESS_CODE, ""));
								} else {
									subscription(queryDocument);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (!emitter.isCancelled()) {
							emitter.onError(e);
						}
					}

				}
			});
		} catch (Exception e) {
			if (!emitter.isCancelled()) {
				emitter.onError(e);
			}
		}
	}

	private void pushDoc(final String queryDocument, final FlowableEmitter<CoreKitBean<T>> emitter){
		CoreKitLogUtils.e("join=>already join");
		ObjectNode payload = objectMapper.createObjectNode();
		payload.put("query", queryDocument);
		try {
			channel.push("doc", payload).receive("ok", new IMessageCallback() {
				@Override
				public void onMessage(CoreKitMsgBean msgBean) {
					CoreKitLogUtils.e("doc=>onMessage=>" + msgBean);
					// update subscriptionId for channel
					subscriptionId = msgBean.getPayload().get("response").get("subscriptionId").asText();
				}
			});
		} catch (Exception e) {
			if (!emitter.isCancelled()) {
				emitter.onError(e);
			}
		}
	}


	public void leaveChannel() {
		if (channel != null) {
			try {
				channel.leave();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class CustomClientFactory<T> extends ViewModelProvider.NewInstanceFactory {

		private ABCoreKitClient mABCoreKitClient;
		private Class<T> tClass;

		public CustomClientFactory(ABCoreKitClient aBCoreKitClient, Class<T> tClass) {
			this.mABCoreKitClient = aBCoreKitClient;
			this.tClass = tClass;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitSubViewModel(mABCoreKitClient, tClass);
		}
	}

	public static class DefaultFactory<T> extends ViewModelProvider.NewInstanceFactory {

		private Context mContext;
		private int apiType;
		private Class<T> tClass;

		public DefaultFactory(Context context, int apiType, Class<T> tClass) {
			this.mContext = context;
			this.apiType = apiType;
			this.tClass = tClass;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitSubViewModel(mContext, apiType, tClass);
		}
	}


}
