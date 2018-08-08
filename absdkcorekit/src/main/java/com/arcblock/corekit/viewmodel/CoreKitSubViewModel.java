package com.arcblock.corekit.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.socket.Channel;
import com.arcblock.corekit.socket.CoreKitMsgBean;
import com.arcblock.corekit.socket.CoreKitSocket;
import com.arcblock.corekit.socket.IErrorCallback;
import com.arcblock.corekit.socket.IMessageCallback;
import com.arcblock.corekit.socket.ISocketCloseCallback;
import com.arcblock.corekit.socket.ISocketOpenCallback;
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
import io.reactivex.schedulers.Schedulers;

public class CoreKitSubViewModel<T> extends ViewModel {

	private static final String TOPIC = "__absinthe__:control";
	private static final String EVENT = "subscription:data";

	private ABCoreKitClient mABCoreKitClient;
	private MutableLiveData<CoreKitBean<T>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();
	private CoreKitSocket mCoreKitSocket;
	private Channel channel;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private Class<T> tClass;

	public CoreKitSubViewModel(Context context, int apiType, Class<T> tClass) {
		this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context, apiType);
		mCoreKitSocket = mABCoreKitClient.initCoreKitSocket();
		this.tClass = tClass;
	}

	public CoreKitSubViewModel(ABCoreKitClient aBCoreKitClient, Class<T> tClass) {
		this.mABCoreKitClient = aBCoreKitClient;
		mCoreKitSocket = mABCoreKitClient.initCoreKitSocket();
		this.tClass = tClass;
	}

	public MutableLiveData<CoreKitBean<T>> subscription(String queryDocument) {

		makeFlow(queryDocument)
				.subscribeOn(Schedulers.io())
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
				try {
					mCoreKitSocket.onOpen(new ISocketOpenCallback() {
						@Override
						public void onOpen() {
							CoreKitLogUtils.e("onOpen");
							channel = mCoreKitSocket.chan(TOPIC, null);
							try {
								channel.join().receive("ok", new IMessageCallback() {
									@Override
									public void onMessage(final CoreKitMsgBean msgBean) {
										CoreKitLogUtils.e("join=>onMessage=>" + msgBean);
										ObjectNode payload = objectMapper.createObjectNode();
										payload.put("query", queryDocument);
										try {
											channel.push("doc", payload).receive("ok", new IMessageCallback() {
												@Override
												public void onMessage(CoreKitMsgBean msgBean) {
													CoreKitLogUtils.e("doc=>onMessage=>" + msgBean);
													// update subscriptionId for channel
													String subscriptionId = msgBean.getPayload().get("response").get("subscriptionId").asText();
													channel.setSubscriptionId(subscriptionId);
												}
											});
										} catch (Exception e) {
											if (!emitter.isCancelled()) {
												emitter.onError(e);
											}
										}
									}
								});
								channel.on(EVENT, new IMessageCallback() {
									@Override
									public void onMessage(final CoreKitMsgBean msgBean) {
										String data = msgBean.getPayload().get("result").get("data").toString();
										T temp = new Gson().fromJson(data, tClass);
										if (!emitter.isCancelled()) {
											emitter.onNext(new CoreKitBean(temp, CoreKitBean.SUCCESS_CODE, ""));
										} else {
											subscription(queryDocument);
										}
									}
								});
							} catch (Exception e) {
								if (!emitter.isCancelled()) {
									emitter.onError(e);
								}
							}
						}
					}).onClose(new ISocketCloseCallback() {
						@Override
						public void onClose() {
							CoreKitLogUtils.e("Closed");
						}
					}).onError(new IErrorCallback() {
						@Override
						public void onError(final String reason) {
							if (!emitter.isCancelled()) {
								emitter.onError(new Exception("onError=>" + reason));
							}
						}
					}).connect();
				} catch (Exception e) {
					if (!emitter.isCancelled()) {
						emitter.onError(new Exception("Failed to connect" + e.toString()));
					}
				}
			}
		}, BackpressureStrategy.LATEST);
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
