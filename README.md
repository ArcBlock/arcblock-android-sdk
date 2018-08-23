# ArcBlock Android SDK

[![license](https://img.shields.io/badge/API-15+-green.svg?longCache=true&style=flat)](https://android-arsenal.com/api?level=15)  [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE)

[README of Chinese](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/README-CN.md)

> Developers need to have basic [GraphQL](https://graphql.org/) capabilities before accessing the ArcBlock Android SDK. We also provide a fully functional [ArcBlock OCAP Playground](https://ocap.arcblock.io/) where developers can use it to write and test the GraphQL statements they want.

## Absdkcorekit Library

Absdkcorekit Library is on the basis of the [apollo-android](https://github.com/apollographql/apollo-android) encapsulated ` Data ` layer core Library, we introduced the Android's latest ` Architecture Components `, among them the ` LiveData ` and ` ViewModel ` and ` Apollo - android Library ` combined with encapsulated into ` CoreKitViewModel `.

#### 1. Import Absdkcorekit Library

Add the following code to the project root directory `build.gradle` file:

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

Add the following code into the app module `build.gradle` file:

``` groovy
apply plugin: 'com.apollographql.android'

//......
dependencies {
  // x.x.x => tag version
  def absdkcorekitversion = "x.x.x" 
  implementation("com.arcblock.corekit:absdkcorekit:$absdkcorekitversion:release@aar"){
	transitive = true
  }
}
```

#### 2. Create graphql directory and download add `schema.json` and create `.graphql` file

> Recommend to create a directory which is the same as app module package name, such as sample code of `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/`，`arcblock-android-sdk/app/src/main/graphql/` behind relatively catalogue and sample project package name is consistent, Of course, you can also specify directory correlation, please refer to the [explicit-schema-location](https://github.com/apollographql/apollo-android#explicit-schema-location) .

1. `schema.json` download address: [bitcoin.json](https://ocap.arcblock.io/doc/bitcoin.json) download later renamed `schema.json`, you can be in the sample project of `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/` directory to find this file, you can directly copy to use. 
2. Using [ArcBlock OCAP Playground](https://ocap.arcblock.io/)  write and test by GraphQL statements, and make a copy of it to a `.graphql` file, you can be in the sample project of `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/` directory to find similar sample files.
3. Build your project, after successful compilation, will you be in `build` found directory compiled automatically generated `Java` code, you can be in the sample project of `arcblock-android-sdk/app/build/generated/source/apollo/` directory to see the generated code, you don't need to modify the automatically generated code.

#### 3. Implement common Query function

1. First of all, set a `CoreKitBeanMapper` object, and use step on your generated code to create a `Query` object, such as:

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
	AccountByAddressQuery query = AccountByAddressQuery.builder().address(address).build();
	```

2. The second step, you need to create a `CoreKitViewModel Factory` object, is used to build next `CoreKitViewModel`, such as:

	```java
	// init the ViewModel with DefaultFactory
	CoreKitViewModel.DefaultFactory factory = new CoreKitViewModel.DefaultFactory(query,accountMapper,DemoApplication.getInstance(),CoreKitConfig.API_TYPE_BTC);
	```
	
	*or*
	
	```java
	// init the ViewModel with CustomClientFactory
	CoreKitViewModel.CustomClientFactory factory = new CoreKitViewModel.CustomClientFactory(query,accountMapper,DemoApplication.getInstance().abCoreKitClient());
	```

	Second way incoming is a custom `ABCoreKitClient` object, and `DefaultFactory` simply pass in a `Application` object can, we will be in ` ABCoreKitClient` instance in a default `ABCoreKitClient` object for `CoreKitViewModel` use.

3. The third step, build `CoreKitViewModel` get `LiveData` object and set the `observe` event monitoring, such as:

	```java
	mBlockByHashQueryViewModel = CoreKitViewModel.getInstance(this, factory);
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

	At this point you have completed a complete process of query data, by use `Absdkcorekit Library`, you only need to write business codes related `GraphQL` statements and instantiate a ` Query` object, and then combined with `CoreKitViewModel` can perfect the implementation of data request and display, you do not need to pay attention to the data layer just focus on the business itself function code development.
	
	> Note: you don't need to care about the whole process of memory release problem, thanks to we use the `ViewModel` components
	
#### 4. Implement paging Query function

1. Build a `CoreKitPagedHelper` object, the inside of the need to implement three return `Query` object method, respectively is:
	1. **getInitialQuery()**：Return a initialize request `Query` object
	2. **getLoadMoreQuery()**：Return a next page request `Query` object
	3. **getRefreshQuery()**：Return a refresh page request `Query` object，normally the query is the same as `getInitialQuery()`
	
	In addition, `CoreKitPagedHelper` object has two attributes: `isHaveMore`, `cursor` respectively used to control whether to have more data and used to save query page data `cursor`
	
	Sample code:
	
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

2. Set `CoreKitBeanMapper ` object, inside need to manually to realize the transformation of the data, and maintain `CoreKitPagedHelper` of `isHaveMore`, `cursor`, sample code:
	
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

3. Create a `CoreKitPagedViewModel Factory` object, is used to build next `CoreKitPagedViewModel`, likewise, it also provides a `CustomClientFactory` and `DefaultFactory`, like [3. Implement common Query function](#3-implement-common-query-function). Sample code:

	```java
	CoreKitPagedViewModel.CustomClientFactory factory = new CoreKitPagedViewModel.CustomClientFactory(blocksMapper, coreKitPagedHelper, DemoApplication.getInstance().abCoreKitClient());
	```

4. Third step, build `CoreKitPagedViewModel` get `LiveData` object and set the `observe` listen for an event, you can implement your own data in the callback listener logic and view logic, sample code:

	```java
	mBlocksByHeightQueryViewModel = CoreKitPagedViewModel.getInstance(this, factory);
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

#### 5. 实现 Subscription

1. Open the socket switch when init the ABCoreClient :

	```java
	ABCoreKitClient.xxx
			.xxxx
			.setOpenSocket(true) // the socket switch
			.xxxx
			.build();
	```

2. Refer to the steps above to build the `CoreKitSubViewModel` object:

	```java
	NewBlockMinedSubscription newBlockMinedSubscription = new NewBlockMinedSubscription();
	CoreKitSubViewModel.CustomClientFactory<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> factory =
				new CoreKitSubViewModel.CustomClientFactory<>(DemoApplication.getInstance().abCoreKitClientEth(), newBlockMinedSubscription, NewBlockMinedSubscription.Data.class);
	mDataCoreKitSubViewModel = CoreKitSubViewModel.getInstance(this, factory);
	```

3. Through ` CoreKitSubViewModel ` object access ` LiveData ` object, and set the ` Observer ` listening, acquiring real-time data from monitoring the callback, and use them to finish their business logic:

	```java
	mDataCoreKitSubViewModel.subscription()
				.setCoreKitSubCallBack(new CoreKitSubViewModel.CoreKitSubCallBack<NewBlockMinedSubscription.Data>() {
					@Override
					public void onNewData(CoreKitBean<NewBlockMinedSubscription.Data> coreKitBean) {
						if (coreKitBean != null && coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
							// set data to view
						}
					}
				});
	```

#### 6. Other Settings

1. `CustomType` Setting：
	1. First, add `customTypeMapping` in the `build.gradle` file of `app module`:
		
		```groovy
		apollo {
		  customTypeMapping['DateTime'] = "java.util.Date"
		}
		```
		
	2. Create the corresponding `CustomTypeAdapter` used to resolve the corresponding `CustomType` :
	
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
		
2. `ABCoreKitClient` initialization:
	
	Recommended in the main process of ` Application onCreate ` method initializes a global singleton ` ABCoreKitClient ` object:
	
	```java
	mABCoreClient = ABCoreKitClient.builder(this)
		.addCustomTypeAdapter(CustomType.DATETIME, dateCustomTypeAdapter)
		.setOkHttpClient(okHttpClient)
		.setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
		.build();
	```
	
	At the time of initialization, you can pass in custom `okHttpClient`, `CustomTypeAdapter`, `ResponseFetcher` parameters.

## License

ArcBlockSDK is available under the MIT license. See the [LICENSE file](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE) for more info.


