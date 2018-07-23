# ArcBlock Android SDK

[![license](https://img.shields.io/badge/API-14+-green.svg?longCache=true&style=flat)](https://android-arsenal.com/api?level=14)  [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE)

> 在接入 ArcBlock Android SDK 前我们需要开发者具备 [GraphQL](https://graphql.org/) 的基础使用能力 。 我们也提供了一个功能完善的 [ArcBlock OCAP Playground](https://ocap.arcblock.io/) ，开发者可以使用它编写和测试自己想要的 GraphQL 语句 。

ArcBlock Android SDK 目前提供了 `Absdkcorekit Library` ，未来还将提供
`AbsdkAccountKit Library` , `AbsdkMessagingKit Library` 。使用这些库可以让 Android 开发者快速的开发出区块链应用 。

## Absdkcorekit Library

Absdkcorekit Library 是在 [apollo-android](https://github.com/apollographql/apollo-android) 的基础上封装的 `Data` 层核心库，我们引入了 Android 最新发布的 `Architecture Components` ，把其中的 `LiveData` 和 `ViewModel` 与 `apollo-android Library` 结合封装成 `CoreKitViewModel` 。

#### 1. 引入 Absdkcorekit Library

添加下面代码到项目根目录的 `build.gradle` 文件中：

``` groovy
buildscript {
  repositories {
    // ...
    maven { url "http://android-docs.arcblock.io/release" }
  }
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

``` groovy
apply plugin: 'com.apollographql.android'

//......

dependencies {
	def absdkcorekitversion = "0.0.9"
	implementation("com.arcblock.corekit:absdkcorekit:$absdkcorekitversion:release@aar"){
		transitive = true
	}
	def lifecycle_version = "1.1.1"
	implementation "android.arch.lifecycle:extensions:$lifecycle_version"
	implementation "android.arch.lifecycle:runtime:$lifecycle_version"
}
```

#### 2. 创建 graphql 目录并下载添加 `schema.json` 和 创建 .graphql 文件

> 推荐创建一个和你 app module 包名相同的目录来存放`schema.json` 和 .graphql 代码，类如示例代码的 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/`， `arcblock-android-sdk/app/src/main/graphql/` 后面的相对目录和示例项目的包名是一致的，当然你也可以指定目录相关，具体请参考：[explicit-schema-location](https://github.com/apollographql/apollo-android#explicit-schema-location)

1. `schema.json` 的下载地址：[bitcoin.json](https://ocap.arcblock.io/doc/bitcoin.json) 下载之后重命名为 `schema.json` ，你可以在示例项目的 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/` 目录下找到此文件，可以直接复制使用 。
2. 使用 [ArcBlock OCAP Playground](https://ocap.arcblock.io/) 编写一个测试通过的 GraphQL 语句，并把它复制到一个 `.graphql` 的文件中，你可以在示例项目的 `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/` 目录下找到类似的示例文件 。
3. 编译你的项目，编译成功之后，你会在 `build` 目录下找到自动编译生成的 `Java` 代码，你可以在示例项目的 `arcblock-android-sdk/app/build/generated/source/apollo/` 目录下看到生成的代码，你不需要修改这些自动生成的代码。

#### 3. 实现普通的 Query 功能
1. 首先，设置一个 `CoreKitBeanMapper` 对象，并你利用上一步生成的代码创建一个 `Query` 对象，如：

	```java
	// init data mapper
	CoreKitBeanMapper<Response<AccountByAddressQuery.Data>, AccountByAddressQuery.AccountByAddress> accountMapper = new CoreKitBeanMapper<Response<AccountByAddressQuery.Data>, AccountByAddressQuery.AccountByAddress>() {

	@Override
	public AccountByAddressQuery.AccountByAddress map(Response<AccountByAddressQuery.Data> dataResponse) {
		if (dataResponse != null) {
			return dataResponse.data().getAccountByAddress();
		}
		return null;
	}
	};
	```
	
	*and*

	```java
	// init a query
	AccountByAddressQuery query = 	AccountByAddressQuery.builder().address(address).build();
	```
	
2. 第二步，你需要创建一个 `CoreKitViewModel Factory` 对象，用来给下一步构建 `CoreKitViewModel`，如：

	```java
	// init the ViewModel with DefaultFactory
	CoreKitViewModel.DefaultFactory factory = new CoreKitViewModel.DefaultFactory(accountMapper,DemoApplication.getInstance());
	```
	
	*or*
	
	```java
	// init the ViewModel with CustomClientFactory
	CoreKitViewModel.CustomClientFactory factory = new CoreKitViewModel.CustomClientFactory(accountMapper,DemoApplication.getInstance().abCoreKitClient());
	```
	
	第二种方式传入的是一个自定义的 `ABCoreKitClient` 对象，而 `DefaultFactory` 只需传入一个 `Application` 对象即可，我们会在 `ABCoreKitClient` 中实例一个默认的 `ABCoreKitClient` 对象供 `CoreKitViewModel` 使用。

3. 第三步，构建 `CoreKitViewModel` 获得 `LiveData` 对象并设置 `observe` 监听事件，如：

	```java
	mBlockByHashQueryViewModel = ViewModelProviders.of(this, factory).get(CoreKitViewModel.class);
	// get livedata and set observe
	mBlockByHashQueryViewModel.getQueryData(query).observe(this, new Observer<CoreKitBean<Response<BlockByHashQuery.Data>>>() {
		@Override
		public void onChanged(@Nullable CoreKitBean<Response<BlockByHashQuery.Data>> coreKitBean) {
			if (coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
				// do view bind data logic
			} else {
				// show error msg
			}
		}
	});
	```
	
	至此你已经完成了一个完整流程的对接，使用 `Absdkcorekit Library` ，你只需编写业务相关的 `GraphQL` 语句并实例化一个`Query` 对象，然后结合 `CoreKitViewModel` 就可以完美的实现数据的获取与展示，你无需关注底层只需专注于本身业务功能代码的开发。

	> Note：整个过程你无需关心内存释放问题，这要归功于我们使用了 `ViewModel` 组件

#### 4. 实现分页的 Query 功能

1. 构建一个 `CoreKitPagedHelper` 对象，需要实现里面的三个返回 `Query` 对象的方法，分别为：
	1. **getInitialQuery()**：返回页面初始化请求的 `Query` 对象
	2. **getLoadMoreQuery()**：返回用于加载一下页数据的 `Query` 对象
	3. **getRefreshQuery()**：返回用于刷新的 `Query` 对象，一般情况下与 `getInitialQuery()` 返回一致
	
	另外，`CoreKitPagedHelper` 对象还有两个属性：`isHaveMore` , `cursor` 用来分别控制是否有更多数据以及用于保存查询下一页数据的 `cursor`
	
	示例代码：
	
	```java
	CoreKitPagedHelper coreKitPagedHelper = new CoreKitPagedHelper() {

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
		};
	```
	
2. 设置 `CoreKitBeanMapper` 对象，在里面需要手动实现数据的转换，并维护 `CoreKitPagedHelper` 的`isHaveMore` , `cursor` ，示例代码如下：
	
	```java
	CoreKitBeanMapper<Response<BlocksByHeightQuery.Data>, List<BlocksByHeightQuery.Datum>> blocksMapper = new CoreKitBeanMapper<Response<BlocksByHeightQuery.Data>, List<BlocksByHeightQuery.Datum>>() {

	@Override
	public List<BlocksByHeightQuery.Datum> map(Response<BlocksByHeightQuery.Data> dataResponse) {
		if (dataResponse != null && dataResponse.data().getBlocksByHeight() != null) {
			// set page info to CoreKitPagedHelper
			if (dataResponse.data().getBlocksByHeight().getPage() != null) {
				// set is have next flag to CoreKitPagedHelper
				coreKitPagedHelper.setHaveMore(dataResponse.data().getBlocksByHeight().getPage().isNext());
				// set new cursor to CoreKitPagedHelper
				coreKitPagedHelper.setCursor(dataResponse.data().getBlocksByHeight().getPage().getCursor());
			}
			return dataResponse.data().getBlocksByHeight().getData();
		}
		return null;
	}
	};
	```
	
3. 创建一个 `CoreKitPagedViewModel Factory` 对象，用来给下一步构建 `CoreKitPagedViewModel`，同样的，它也提供了 `CustomClientFactory` 和 `DefaultFactory` 用法和 [3. 实现普通的Query功能](#3-实现普通的query功能) 中类似。示例代码：
	
	```java
	CoreKitPagedViewModel.CustomClientFactory factory = new CoreKitPagedViewModel.CustomClientFactory(blocksMapper, coreKitPagedHelper, DemoApplication.getInstance().abCoreKitClient());
	```
	
4. 第三步，构建 `CoreKitPagedViewModel` 获得 `LiveData` 对象并设置 `observe` 监听事件，可以在回调监听中实现自己的数据逻辑和视图逻辑，示例代码：
	
	```java
	mBlocksByHeightQueryViewModel = ViewModelProviders.of(this, factory).get(CoreKitPagedViewModel.class);
	mBlocksByHeightQueryViewModel.getCleanQueryData().observe(this, new Observer<CoreKitPagedBean<List<BlocksByHeightQuery.Datum>>>() {
	@Override
	public void onChanged(@Nullable CoreKitPagedBean<List<BlocksByHeightQuery.Datum>> coreKitPagedBean) {
		//1. handle return data
		if (coreKitPagedBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
			if (coreKitPagedBean.getData() != null) {
				// new a old list
				List<BlocksByHeightQuery.Datum> oldList = new ArrayList<>();
				oldList.addAll(mBlocks);

				// set mBlocks with new data
				mBlocks = coreKitPagedBean.getData();
				DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CoreKitDiffUtil<>(oldList, mBlocks), true);
				// need this line , otherwise the update will have no effect
				mListBlocksAdapter.setNewListData(mBlocks);
				result.dispatchUpdatesTo(mListBlocksAdapter);
			}
		}

		//2. view status change and loadMore component need
		content.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		content.setRefreshing(false);
		if (coreKitPagedHelper.isHaveMore()) {
			mListBlocksAdapter.setEnableLoadMore(true);
			mListBlocksAdapter.loadMoreComplete();
		} else {
			mListBlocksAdapter.loadMoreEnd();
		}
	}
	});
	```


## License

ArcBlockSDK is available under the MIT license. See the [LICENSE file](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE) for more info.

