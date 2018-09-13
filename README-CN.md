# ArcBlock Android SDK

[![license](https://img.shields.io/badge/API-15+-green.svg?longCache=true&style=flat)](https://android-arsenal.com/api?level=14)  [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE)

> 在接入 ArcBlock Android SDK 前需要开发者具备 [GraphQL](https://graphql.org/) 的基础使用能力 。 我们提供了一个功能完善的 [ArcBlock OCAP Playground](https://ocap.arcblock.io/) ，开发者可以使用它编写和测试自己想要的 GraphQL 语句 。

ArcBlock Android SDK 目前提供了 `Absdkcorekit Library` ，未来还将提供
`AbsdkAccountKit Library` , `AbsdkMessagingKit Library` 。使用这些库可以让 Android 开发者快速的开发出区块链应用 。

## Absdkcorekit Library

Absdkcorekit Library 是在 [apollo-android](https://github.com/apollographql/apollo-android) 的基础上封装的 `Data` 层核心库，我们引入了 Android 最新发布的 `Architecture Components` ，把其中的 `LiveData` 和 `ViewModel` 与 `apollo-android Library` 结合封装成 `CoreKitViewModel` 。

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

1. `schema.json` 的下载地址：[bitcoin.json](https://ocap.arcblock.io/doc/bitcoin.json) 下载之后重命名为 `schema.json` ，你可以在示例项目的 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/` 目录下找到此文件，可以直接复制使用 。
2. 使用 [ArcBlock OCAP Playground](https://ocap.arcblock.io/) 编写一个测试通过的 GraphQL 语句，并把它复制到一个 `.graphql` 的文件中，你可以在示例项目的 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/` 目录下找到类似的示例文件 。
3. 编译你的项目，编译成功之后，你会在 `build` 目录下找到自动编译生成的 `Java` 代码，你可以在示例项目的 `arcblock-android-sdk/app/build/generated/source/apollo/` 目录下看到生成的代码，你不需要修改这些自动生成的代码。

#### 3. 实现普通的查询功能

1. 首先，自定义一个类继承自 CoreKitQuery 抽象类，需要实现两个部分：
	- **构造方法：** 实现和当前使用相匹配的构造方法，匹配条件取决于是在 FragmentActivity 中还是 Fragment 中使用的此 Query 和 当前传入的是自定义的 ABCoreKitClient 还是默认的 ABCoreKitClient
	- **map(...) 方法：** 该方法是实现 CoreKitBeanMapperInterface 的一个方法，供 CoreKitQueryViewModel 内部使用，用于将返回的 Response map 成最终想得到的数据格式
	- **getQuery() 方法：** 初始化并返回一个当前的 Query 对象，用于实现具体的业务查询

	示例代码：

	```java
	/**
     * AccountByAddressQueryHelper for AccountByAddressQuery
     */
    private class AccountByAddressQueryHelper extends CoreKitQuery<AccountByAddressQuery.Data, AccountByAddressQuery.AccountByAddress> {

        public AccountByAddressQueryHelper(FragmentActivity activity, LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
            super(activity, lifecycleOwner, client);
        }

        @Override
        public AccountByAddressQuery.AccountByAddress map(Response<AccountByAddressQuery.Data> dataResponse) {
            if (dataResponse != null) {
                return dataResponse.data().getAccountByAddress();
            }
            return null;
        }

        @Override
        public Query getQuery() {
            return AccountByAddressQuery.builder().address(address).build();
        }
    }
	```

	> 这边的命名建议以对应的 `Query`, `Mutaition`, `Subscription` 具体类名称加上 `-Helper` 结尾，比如上面 AccountByAddressQuery 对应的为 AccountByAddressQueryHelper

2. 第二步，创建一个 `xxxHelper` 查询类的对象，并设置 Observe 对象，请求并获取数据

	- 创建 `xxxHelper` 查询类的对象：
	
		```java
		AccountByAddressQueryHelper accountByAddressQueryHelper = new AccountByAddressQueryHelper(this, this, DemoApplication.getInstance().abCoreKitClientBtc());
		```

		这里的构造函数上文已经提过，有4种不同的实现可以选择，具体根据自己的代码选择即可。

	- 设置 Observe 对象：
	
		```java
		accountByAddressQueryHelper.setObserve(new Observer<CoreKitBean<AccountByAddressQuery.AccountByAddress>>() {
				@Override
				public void onChanged(@Nullable CoreKitBean<AccountByAddressQuery.AccountByAddress> coreKitBean) {
					if (coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
						AccountByAddressQuery.AccountByAddress accountByAddress = coreKitBean.getData();
						// get data and set data to view here.
					} else {
						// show error msg.
					}
				}
			});
		```

	> Note：整个过程你无需关心内存释放问题，这要归功于我们在内部使用了 `ViewModel` 组件

#### 4. 实现分页查询功能

1. 首先，自定义一个类继承自 CoreKitPagedQuery 抽象类，需要实现五个部分：
	- **构造方法：** 实现和当前使用相匹配的构造方法，匹配条件取决于是在 FragmentActivity 中还是 Fragment 中使用的此 Query 和 当前传入的是自定义的 ABCoreKitClient 还是默认的 ABCoreKitClient
	- **map(...) 方法：** 该方法是实现 CoreKitBeanMapperInterface 的一个方法，供 CoreKitQueryViewModel 内部使用，用于将返回的 Response map 成最终想得到的数据格式
		> 这里不同于普通查询的地方是，在分页查询的 map(...) 方法中，需要手动地设置 `setHasMore(boolean hasMore)` 和 `setCursor(String cursor)`，这两个参数是底层判断是否进行分页请求的依据
	- **getInitialQuery() 方法：** 初始化并返回一个分页查询的初始的 Query 对象
	- **getLoadMoreQuery() 方法：** 初始化并返回一个分页查询查询更多的 Query 对象
	- **getRefreshQuery() 方法：** 初始化并返回一个分页查询刷新查询的 Query 对象，一般情况下此 Query 对象与 getInitialQuery() 返回的相同

	示例代码：

	```java
	/**
     *  BlocksByHeightQueryHelper for BlocksByHeightQuery
     */
    private class BlocksByHeightQueryHelper extends CoreKitPagedQuery<BlocksByHeightQuery.Data, BlocksByHeightQuery.Datum> {

        public BlocksByHeightQueryHelper(FragmentActivity activity, LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
            super(activity, lifecycleOwner, client);
        }

        @Override
        public List<BlocksByHeightQuery.Datum> map(Response<BlocksByHeightQuery.Data> dataResponse) {
            if (dataResponse != null && dataResponse.data().getBlocksByHeight() != null) {
                // set page info to CoreKitPagedQuery
                if (dataResponse.data().getBlocksByHeight().getPage() != null) {
                    // set is have next flag to CoreKitPagedQuery
                    setHasMore(dataResponse.data().getBlocksByHeight().getPage().isNext());
                    // set new cursor to CoreKitPagedQuery
                    setCursor(dataResponse.data().getBlocksByHeight().getPage().getCursor());
                }
                return dataResponse.data().getBlocksByHeight().getData();
            }
            return null;
        }

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
        public Query getRefreshQuery() {
            return BlocksByHeightQuery.builder().fromHeight(startIndex).toHeight(endIndex).build();
        }
    }
	```

	> 这边的命名建议以对应的 `Query`, `Mutaition`, `Subscription` 具体类名称加上 `-Helper` 结尾，比如上面 BlocksByHeightQuery 对应的为 BlocksByHeightQueryHelper

2. 第二步，创建一个 `xxxHelper` 查询类的对象，并设置 Observe 对象，请求并获取数据

	- 创建 xxxHelper 查询类的对象：
	
		```java
		mBlocksByHeightQueryHelper = new BlocksByHeightQueryHelper(this, this, DemoApplication.getInstance().abCoreKitClientBtc());
		```

		这里的构造函数上文已经提过，有4种不同的实现可以选择，具体根据自己的代码选择即可。
	
	- 设置 Observe 对象：

		```java
		mBlocksByHeightQueryHelper.setObserve(new Observer<CoreKitPagedBean<List<BlocksByHeightQuery.Datum>>>() {
				@Override
				public void onChanged(@Nullable CoreKitPagedBean<List<BlocksByHeightQuery.Datum>> coreKitPagedBean) {
					if (coreKitPagedBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
						if (coreKitPagedBean.getData() != null) {
						// get data and set data to view here.
						}
					}
				}
			});
		```

	- 调用 refresh 方法刷新

		```java
		mBlocksByHeightQueryHelper.refresh();
		```

	- 调用 loadMore 方法加载下一页数据

		```java
		mBlocksByHeightQueryHelper.loadMore();
		```

#### 5. 实现数据订阅

1. 首先，在 ABCoreClient 初始化的时候打开 socket 开关:

	```java
	ABCoreKitClient.xxx
			.xxxx
			.setOpenSocket(true)
			.xxxx
			.build();
	```

2. 第二步，自定义一个类继承自 CoreKitSubscription 抽象类，需要实现三个部分：

	- **构造方法：** 实现和当前使用相匹配的构造方法，匹配条件取决于是在 FragmentActivity 中还是 Fragment 中使用的此 Query 和 当前传入的是自定义的 ABCoreKitClient 还是默认的 ABCoreKitClient
	- **getSubscription() 方法：** 初始化并返回一个具体的 Subscription 对象
	- **getResultDataClass() 方法：** 返回最终期望的 Data 类的 Class，供 CoreKitSubscriptionViewModel 中 json 解析使用

	示例代码：

	```java
	/**
     * NewBlockMinedSubscriptionHelper for NewBlockMinedSubscription
     */
    private class NewBlockMinedSubscriptionHelper extends CoreKitSubscription<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> {

        public NewBlockMinedSubscriptionHelper(FragmentActivity activity, ABCoreKitClient client) {
            super(activity, client);
        }

        @Override
        public NewBlockMinedSubscription getSubscription() {
            return new NewBlockMinedSubscription();
        }

        @Override
        public Class<NewBlockMinedSubscription.Data> getResultDataClass() {
            return NewBlockMinedSubscription.Data.class;
        }
    }
	```

	> 这边的命名建议以对应的 `Query`, `Mutaition`, `Subscription` 具体类名称加上 `-Helper` 结尾，比如上面 NewBlockMinedSubscription 对应的为 NewBlockMinedSubscriptionHelper

3. 第三步，设置 CoreKitSubCallBack 与 CoreKitSocketStatusCallBack

	- 设置 CoreKitSubCallBack

		```java
		// add data callback
		mNewBlockMinedSubscriptionHelper.setCoreKitSubCallBack(new CoreKitSubscriptionViewModel.CoreKitSubCallBack<NewBlockMinedSubscription.Data>() {
			@Override
			public void onNewData(CoreKitBean<NewBlockMinedSubscription.Data> coreKitBean) {
				if (coreKitBean != null && coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
					// get data and set data to view here.
				}
			}
		});
		```
	- 设置 CoreKitSocketStatusCallBack

		```java
		// add status callback
		mNewBlockMinedSubscriptionHelper.setCoreKitSocketStatusCallBack(new CoreKitSocketStatusCallBack() {
			@Override
			public void onOpen() {
			    // do something here when socket on open
			}

			@Override
			public void onClose() {
			    // do something here when socket on close
			}

			@Override
			public void onError() {
			    // do something here when on error
			}
		});
		```

#### 6. 其他配置

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
	mABCoreClient = ABCoreKitClient.builder(this)
		.addCustomTypeAdapter(CustomType.DATETIME, dateCustomTypeAdapter)
		.setOkHttpClient(okHttpClient)
		.setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
		.build();
	```
	
	在初始化的时候，你可以传入自定义的 `okHttpClient` ，`CustomTypeAdapter` ，`ResponseFetcher` 等参数。

## License

ArcBlockSDK is available under the MIT license. See the [LICENSE file](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE) for more info.

