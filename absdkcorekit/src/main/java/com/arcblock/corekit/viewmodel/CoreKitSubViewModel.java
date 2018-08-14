package com.arcblock.corekit.viewmodel;

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

public class CoreKitSubViewModel<T, D extends com.apollographql.apollo.api.Subscription> extends ViewModel {

	private ABCoreKitClient mABCoreKitClient;
	private Channel channel;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private Class<T> tClass;
	private String graphQlSubId;
	private CoreKitSubCallBack<T> mCoreKitSubCallBack;
	private String queryDoc;

	public CoreKitSubViewModel(Context context, int apiType, D graphSub, Class<T> tClass) {
		this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context, apiType);
		this.tClass = tClass;
		channel = mABCoreKitClient.getCoreKitSocket().chan(Channel.CORE_KIT_TOPIC, null);
		graphQlSubId = graphSub.operationId() + "$" + graphSub.variables().valueMap().hashCode();

		mABCoreKitClient.getCoreKitSocket().onOpen(new ISocketOpenCallback() {
			@Override
			public void onOpen() {
				if (!TextUtils.isEmpty(queryDoc)) {
					channel.initStatus();
					subscription(queryDoc);
				}
			}
		});
	}

	public CoreKitSubViewModel(ABCoreKitClient aBCoreKitClient, D graphSub, Class<T> tClass) {
		this.mABCoreKitClient = aBCoreKitClient;
		this.tClass = tClass;
		channel = mABCoreKitClient.getCoreKitSocket().chan(Channel.CORE_KIT_TOPIC, null);
		graphQlSubId = graphSub.operationId() + "$" + graphSub.variables().valueMap().hashCode();

		mABCoreKitClient.getCoreKitSocket().onOpen(new ISocketOpenCallback() {
			@Override
			public void onOpen() {
				CoreKitLogUtils.e("CoreKitSubViewModel********onOpen");
				if (!TextUtils.isEmpty(queryDoc)) {
					channel.initStatus();
					subscription(queryDoc);
				}
			}
		});
	}

	public void setCoreKitSubCallBack(CoreKitSubCallBack<T> coreKitSubCallBack) {
		mCoreKitSubCallBack = coreKitSubCallBack;
	}

	public CoreKitSubViewModel<T, D> subscription(String queryDocument) {
		queryDoc = queryDocument;
		makeFlow(queryDocument)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<CoreKitBean<T>>() {
					@Override
					public void onSubscribe(Subscription s) {
						s.request(128);
					}

					@Override
					public void onNext(CoreKitBean<T> coreKitBean) {
						if (mCoreKitSubCallBack != null && coreKitBean.getData() != null) {
							mCoreKitSubCallBack.onNewData(coreKitBean);
						}
					}


					@Override
					public void onError(Throwable t) {
						if (mCoreKitSubCallBack != null) {
							mCoreKitSubCallBack.onNewData(new CoreKitBean(null, CoreKitBean.FAIL_CODE, t.toString()));
						}
					}

					@Override
					public void onComplete() {

					}
				});
		return this;
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
					boolean isPush = false;

					@Override
					public void onMessage(final CoreKitMsgBean msgBean) {
						CoreKitLogUtils.e("join=>onMessage=>" + msgBean);
						if (!isPush) {
							pushDoc(queryDocument, emitter);
							isPush = true;
						}
					}
				});
			} else {
				// if already join, just push a new doc with different payload
				pushDoc(queryDocument, emitter);
			}
		} catch (Exception e) {
			CoreKitLogUtils.e("initChannel=>" + e.toString());
			if (!emitter.isCancelled()) {
				emitter.onError(e);
			}
		}
	}

	private void pushDoc(final String queryDocument, final FlowableEmitter<CoreKitBean<T>> emitter) {
		if (channel.isNeedPushDoc(graphQlSubId)) {
			CoreKitLogUtils.e("join=>already join");
			ObjectNode payload = objectMapper.createObjectNode();
			payload.put("query", queryDocument);
			try {
				channel.push("doc", payload).receive("ok", new IMessageCallback() {
					@Override
					public void onMessage(CoreKitMsgBean msgBean) {
						CoreKitLogUtils.e("doc=>onMessage=>" + msgBean);
						// update subscriptionId for channel
						channel.setGrahpSubAndSubIdMapItem(graphQlSubId, msgBean.getPayload().get("response").get("subscriptionId").asText());
						setCoreKitEvent(emitter, queryDocument);
					}
				});
			} catch (Exception e) {
				CoreKitLogUtils.e("pushDoc=>" + e.toString());
				if (!emitter.isCancelled()) {
					emitter.onError(e);
				}
			}
		} else {
			CoreKitLogUtils.e("this graphqlId doc already push");
			setCoreKitEvent(emitter, queryDocument);
		}
	}

	private void setCoreKitEvent(final FlowableEmitter<CoreKitBean<T>> emitter, final String queryDocument) {
		CoreKitLogUtils.e("********setCoreKitEvent******");
		channel.on(Channel.CORE_KIT_EVENT, new IMessageCallback() {
			@Override
			public void onMessage(final CoreKitMsgBean msgBean) {
				CoreKitLogUtils.e("channel EVENT onMessage thread name =>" + Thread.currentThread().getName());
				try {
					if (!TextUtils.isEmpty(msgBean.getTopic()) && msgBean.getTopic().startsWith("__absinthe__:doc:")) {
						String data = msgBean.getPayload().get("result").get("data").toString();
						String tempSubId = msgBean.getPayload().get("subscriptionId").asText("");
						if (TextUtils.equals(tempSubId, channel.getGrahpSubAndSubIdMapItemValueByKey(graphQlSubId))) {
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
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		CoreKitLogUtils.e("******onCleared*******");
		leaveChannel();
		channel = null;
		mCoreKitSubCallBack = null;
	}

	public void leaveChannel() {
		if (channel != null) {
			try {
				channel.leave(graphQlSubId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class CustomClientFactory<T, D extends com.apollographql.apollo.api.Subscription> extends ViewModelProvider.NewInstanceFactory {

		private ABCoreKitClient mABCoreKitClient;
		private Class<T> tClass;
		private D graphSub;


		public CustomClientFactory(ABCoreKitClient aBCoreKitClient, D graphSub, Class<T> tClass) {
			this.mABCoreKitClient = aBCoreKitClient;
			this.tClass = tClass;
			this.graphSub = graphSub;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitSubViewModel(mABCoreKitClient, graphSub, tClass);
		}
	}

	public static class DefaultFactory<T, D extends com.apollographql.apollo.api.Subscription> extends ViewModelProvider.NewInstanceFactory {

		private Context mContext;
		private int apiType;
		private Class<T> tClass;
		private D graphSub;

		public DefaultFactory(Context context, int apiType, D graphSub, Class<T> tClass) {
			this.mContext = context;
			this.apiType = apiType;
			this.tClass = tClass;
			this.graphSub = graphSub;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitSubViewModel(mContext, apiType, graphSub, tClass);
		}
	}

	public interface CoreKitSubCallBack<T> {
		void onNewData(CoreKitBean<T> coreKitBean);
	}


}
