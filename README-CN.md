# ArcBlock Android SDK

[![license](https://img.shields.io/badge/API-15+-green.svg?longCache=true&style=flat)](https://android-arsenal.com/api?level=14)  [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE)

> 在接入 ArcBlock Android SDK 前需要开发者具备 [GraphQL](https://graphql.org/) 的基础使用能力 。 我们提供了一个功能完善的 [ArcBlock OCAP Playground](https://ocap.arcblock.io/) ，开发者可以使用它编写和测试自己想要的 GraphQL 语句 。

ArcBlock Android SDK 目前提供了 `Absdkcorekit Library` ，未来还将提供
`AbsdkAccountKit Library` , `AbsdkMessagingKit Library` 。使用这些库可以让 Android 开发者快速的开发出区块链应用 。

## Absdkcorekit Library

Absdkcorekit Library 是在 [apollo-android](https://github.com/apollographql/apollo-android) 的基础上封装的 `Data` 层核心库，我们引入了 `LifecycleObserver` 使得 SDK 可以感知页面的生命周期，在 SDK 层做内存优化的处理, 开发者只需在使用的时候, 传入一个 LifecycleOwner 对象即可 ( support 库中的 Fragment 和 AppCompatActivity 都已经实现了 LifecycleOwner 接口, 可以直接使用, 否则可以参考上面 support 库中的实现自己实现 LifecycleOwner )。

#### 1. 引入 Absdkcorekit Library

添加下面代码到项目根目录的 `build.gradle` 文件中：

```groovy
buildscript {
  dependencies {
    //...
    classpath 'com.apollographql.apollo:apollo-gradle-plugin:1.0.0-alpha'
  }
}

allprojects {
   repositories {
	//...
	maven { url "http://android-docs.arcblock.io/release" }
   }
}
```

添加下面代码到 app module 的 `build.gradle` 文件中：

```groovy
apply plugin: 'com.apollographql.android'

apollo {
    useJavaBeansSemanticNaming = true
}

//......
dependencies {
  // x.x.x => release version
  def absdkcorekitversion = "x.x.x"
  implementation("com.arcblock.corekit:absdkcorekit:$absdkcorekitversion:release@aar"){
	transitive = true
  }
}
```

#### 2. 创建 graphql 目录并下载添加 `schema.json` 和 创建 `.graphql` 文件

> 推荐创建一个和你 app module 包名相同的目录来存放`schema.json` 和 `.graphql` 代码，类如示例代码的 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/`， `arcblock-android-sdk/app/src/main/graphql/` 后面的相对目录和示例项目的包名是一致的，当然你也可以指定目录相关，具体请参考：[explicit-schema-location](https://github.com/apollographql/apollo-android#explicit-schema-location)

1. `schema.json` 的下载地址：[bitcoin.json](https://ocap.arcblock.io/doc/bitcoin.json), [ethereum.json](https://ocap.arcblock.io/doc/ethereum.json) 下载之后都需要重命名为 `schema.json` ，你可以在示例项目的 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/btc/` 与 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/eth/` 目录下找到此文件，可以直接复制使用。
2. 使用 [ArcBlock OCAP Playground](https://ocap.arcblock.io/) 编写一个测试通过的 GraphQL 语句，并把它复制到一个 `.graphql` 的文件中，你可以在示例项目的 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/btc` 或 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/eth/` 目录下找到类似的示例文件 。
3. 编译你的项目，编译成功之后，你会在 `build` 目录下找到自动编译生成的 `Java` 代码，你可以在示例项目的 `arcblock-android-sdk/app/build/generated/source/apollo/` 目录下看到生成的代码，你不需要修改这些自动生成的代码。

#### 3. 实现普通的查询功能

1. new 一个 `CoreKitQuery` 对象：

	```java
	CoreKitQuery coreKitQuery = new CoreKitQuery(this, DemoApplication.getInstance().abCoreKitClientBtc());
	```

2. 发起查询请求，只需在方法中设置好对应的查询对象和回调对象，查询的结果可以在回调对象中拿到：

	```java
	coreKitQuery.query(AccountByAddressQuery.builder().address(address).build(), new CoreKitResultListener<AccountByAddressQuery.Data>() {
		@Override
		public void onSuccess(AccountByAddressQuery.Data data) {
			// 获得数据
		}

		@Override
		public void onError(String errMsg) {
			// 获得错误信息
		}

		@Override
		public void onComplete() {
			// 查询结束
		}
	});
	```

> 一个 CoreKitQuery 对象可以用来进行多个查询对象的处理。

#### 4. 实现分页查询功能

1. new 一个 `PagedQueryHelper` 对象, 这个对象用于构建分页查询用到的初始（刷新）查询对象和加在更多查询对象，以及进行数据的 map 处理，设置分页相关标志：

	```java
	mPagedQueryHelper = new PagedQueryHelper<BlocksByHeightQuery.Data, BlocksByHeightQuery.Datum>() {
		@Override
		public Query getInitialQuery() {
			return BlocksByHeightQuery.builder().fromHeight(startIndex).toHeight(endIndex).build();
		}

		@Override
		public Query getLoadMoreQuery() {
			PageInput pageInput = null;
			if (!TextUtils.isEmpty(getCursor())) {
				pageInput = PageInput.builder().cursor(getCursor()).build();
			}
			return BlocksByHeightQuery.builder().fromHeight(startIndex).toHeight(endIndex).paging(pageInput).build();
		}

		@Override
		public List<BlocksByHeightQuery.Datum> map(BlocksByHeightQuery.Data data) {
			if (data.getBlocksByHeight() != null) {
				// set page info to PagedQueryHelper
				if (data.getBlocksByHeight().getPage() != null) {
					// set is have next flag to PagedQueryHelper
					setHasMore(data.getBlocksByHeight().getPage().isNext());
					// set new cursor to PagedQueryHelper
					setCursor(data.getBlocksByHeight().getPage().getCursor());
				}
				return data.getBlocksByHeight().getData();
			}
			return null;
		}
	};
	```

2. new 一个 `CoreKitPagedQuery` 对象，传入上面的 `PagedQueryHelper` 对象：

	```java
	mCoreKitPagedQuery = new CoreKitPagedQuery(this, DemoApplication.getInstance().abCoreKitClientBtc(), mPagedQueryHelper);
	```

3. 设置分页查询数据处理回调并发起首页查询：

	```java
	mCoreKitPagedQuery.setPagedQueryResultListener(new CoreKitPagedQueryResultListener<BlocksByHeightQuery.Datum>() {
		@Override
		public void onSuccess(List<BlocksByHeightQuery.Datum> datas) {
		  // 处理分页回来的数据，这里会返回总量数据，具体参考 demo 代码
		}

		@Override
		public void onError(String errMsg) {
		  // 处理错误信息
		}

		@Override
		public void onComplete() {
		  // 分页请求结束
		}
	});
	// start initial query
	mCoreKitPagedQuery.startInitQuery();
	```

4. 刷新页面查询：

	```java
	mCoreKitPagedQuery.startInitQuery();
	```

5. 加载下一页查询：

	```java
	mCoreKitPagedQuery.startLoadMoreQuery();
	```

> 区别于 `CoreKitQuery`， 一个 `CoreKitPagedQuery` 对象只能服务于一个特定的分页查询。

#### 5. 实现 mutation 功能

1. new 一个 `CoreKitMutation` 对象：

	```java
	CoreKitMutation coreKitMutation = new CoreKitMutation(this, DemoApplication.getInstance().abCoreKitClient());
	```

2. 发起 mutation 请求，只需在方法中设置好对应的 mutation 对象和回调对象，mutation 的结果可以在回调对象中拿到：

	```java
	coreKitMutation.mutation(mutation, new CoreKitResultListener<XXMutation.Data>() {
			@Override
			public void onSuccess(XXMutation.Data data) {
				// 获得数据
			}

			@Override
			public void onError(String errMsg) {
				// 获得错误信息
			}

			@Override
			public void onComplete() {
				// 查询结束
			}
		});
	```

> 一个 CoreKitMutation 对象可以用来进行多个 mutation 对象的处理。

#### 6. 实现数据订阅功能

1. 首先，在 ABCoreClient 初始化的时候打开 socket 开关:

	```java
	ABCoreKitClient.xxx
			.xxxx
			.setOpenSocket(true)
			.xxxx
			.build();
	```

2. new 一个 `CoreKitSubscription` 对象：

	```java
	mCoreKitSubscription = new CoreKitSubscription<>(this, DemoApplication.getInstance().abCoreKitClientEth(), new NewBlockMinedSubscription(), NewBlockMinedSubscription.Data.class);
	```

3. 设置 ResultListener：

	```java
	mCoreKitSubscription.setResultListener(new CoreKitSubscriptionResultListener<NewBlockMinedSubscription.Data>() {
		@Override
		public void onSuccess(NewBlockMinedSubscription.Data data) {
			// 处理数据
		}

		@Override
		public void onError(String errMsg) {
			// 处理错误信息
		}
	});
	```

4. 设置 CoreKitSocketStatusCallBack：

	```java
	mCoreKitSubscription.setCoreKitSocketStatusCallBack(new CoreKitSocketStatusCallBack() {
		@Override
		public void onOpen() {
			// socket打开
		}

		@Override
		public void onClose() {
			// socket关闭
		}

		@Override
		public void onError() {
			// 发生错误
		}
	});
	```

> 一个 CoreKitSubscription 对象只能服务于一个特定的 Subscription 对象。

#### 7. 其他配置

1. `CustomType` 配置：

	1. 首先，需要在 `app module` 的 `build.gradle` 文件中添加 `customTypeMapping` :
		
		```groovy
		apollo {
		  customTypeMapping['DateTime'] = "java.util.Date"
		}
		```
		
	2. 创建对应的 `CustomTypeAdapter` 用于解析对应的 `CustomType` ：
		
		```java
		CustomTypeAdapter dateCustomTypeAdapter = new CustomTypeAdapter<Date>() {

		@Override
		public Date decode(CustomTypeValue value) {
			try {
				SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000000'Z'");
				utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//时区定义并进行时间获取
				Date gpsUTCDate = utcFormat.parse(value.value.toString());
				return gpsUTCDate;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public CustomTypeValue encode(Date value) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000000'Z'");
			return new CustomTypeValue.GraphQLString(sdf.format(value));
		}
		};
		```
		
2. `ABCoreKitClient` 初始化：
	
	在主进程的 `Application onCreate` 方法中初始化一个全局单例的 `ABCoreKitClient` 对象：
	
	```java
	mABCoreClientBtc = ABCoreKitClient.builder(this, CoreKitConfig.ApiType.API_TYPE_BTC)
                    .addCustomTypeAdapter(CustomType.DATETIME, dateCustomTypeAdapter)
                    .setOpenOkHttpLog(true)
                    .setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
                    .build();
	```
	
	在初始化的时候，你可以传入自定义的 `okHttpClient` ，`CustomTypeAdapter` ，`ResponseFetcher` 等参数。

## License

ArcBlockSDK is available under the MIT license. See the [LICENSE file](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE) for more info.

