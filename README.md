# ArcBlock Android SDK

[![license](https://img.shields.io/badge/API-15+-green.svg?longCache=true&style=flat)](https://android-arsenal.com/api?level=15)  [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE)

[中文 README](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/README-CN.md)

> Developers need to have basic [GraphQL](https://graphql.org/) capabilities before accessing the ArcBlock Android SDK. We also provide a fully functional [ArcBlock OCAP Playground](https://ocap.arcblock.io/) where developers can use it to write and test the GraphQL statements they want.

## Absdkcorekit Library

Absdkcorekit Library is on the basis of the [apollo-android](https://github.com/apollographql/apollo-android) encapsulated `Data` layer core Library, we introduced `LifecycleObserver` makes the SDK can sense the page life cycle, To do memory optimization in the SDK layer, developers only need to pass in a `LifecycleOwner` object when they are using it. (both the `Fragment` and `AppCompatActivity` in the support library have implemented the `LifecycleOwner` interface, which can be used directly, otherwise, they can realize `LifecycleOwner` by referring to the implementation in the support library above.)

#### 1. Import Absdkcorekit Library

Add the following code to the project root directory `build.gradle` file:

``` groovy
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

Add the following code into the app module `build.gradle` file:

``` groovy
apply plugin: 'com.apollographql.android'

apollo {
    useJavaBeansSemanticNaming = true
}

//......
dependencies {
  implementation 'com.arcblock.corekit:absdkcorekit:0.3.3'
}
```

#### 2. Create graphql directory and download add `schema.json` and create `.graphql` file

> Recommend to create a directory which is the same as app module package name, such as sample code of `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/`，`arcblock-android-sdk/app/src/main/graphql/` behind relatively catalogue and sample project package name is consistent, Of course, you can also specify directory correlation, please refer to the [explicit-schema-location](https://github.com/apollographql/apollo-android#explicit-schema-location) .

1. `schema.json` download address: [bitcoin.json](https://ocap.arcblock.io/doc/bitcoin.json), [ethereum.json](https://ocap.arcblock.io/doc/ethereum.json) download later and all need to be renamed as `schema.json`, you can be in the sample project of `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/btc/` or `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/eth/` directory to find this file, you can directly copy to use. 
2. Using [ArcBlock OCAP Playground](https://ocap.arcblock.io/)  write and test by GraphQL statements, and make a copy of it to a `.graphql` file, you can be in the sample project of `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/btc/` or `arcblock-android-sdk/app/src/main/graphql/com/arcblock/sdk/demo/eth/` directory to find similar sample files.
3. Build your project, after successful compilation, will you be in `build` found directory compiled automatically generated `Java` code, you can be in the sample project of `arcblock-android-sdk/app/build/generated/source/apollo/` directory to see the generated code, you don't need to modify the automatically generated code.

#### 3. Implement common query function

1. New one `CoreKitQuery` object:

	```java
	CoreKitQuery coreKitQuery = new CoreKitQuery(this, DemoApplication.getInstance().abCoreKitClientBtc());
	```

2. To initiate a query request, you only need to set the corresponding query object and callback object in the method, and the results of the query can be obtained in the callback object:

	```java
	coreKitQuery.query(AccountByAddressQuery.builder().address(address).build(), new CoreKitResultListener<AccountByAddressQuery.Data>() {
		@Override
		public void onSuccess(AccountByAddressQuery.Data data) {
			// get the data
		}

		@Override
		public void onError(String errMsg) {
			// get the error message
		}

		@Override
		public void onComplete() {
			// query complete
		}
	});
	```

> A CoreKitQuery object can be used for processing multiple query objects.
	
#### 4. Implement paged Query function

1. New `PagedQueryHelper` object, which is used to build the initial (or refresh) query object used for paging queries and to add more query objects, as well as map processing of the data, sets the paged flag:

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

2. New a ` CoreKitPagedQuery ` object, introduced into the above ` PagedQueryHelper ` objects:

	```java
	mCoreKitPagedQuery = new CoreKitPagedQuery(this, DemoApplication.getInstance().abCoreKitClientBtc(), mPagedQueryHelper);
	```

3. Set the paging query data processing callback and launch the initial page query:

	```java
	mCoreKitPagedQuery.setPagedQueryResultListener(new CoreKitPagedQueryResultListener<BlocksByHeightQuery.Datum>() {
		@Override
		public void onSuccess(List<BlocksByHeightQuery.Datum> datas) {
		  // Processing the data that comes back from paging, the total data will be returned here, please refer to the demo code for details
		}

		@Override
		public void onError(String errMsg) {
		  // get the error message
		}

		@Override
		public void onComplete() {
		  // query complete
		}
	});
	// start initial query
	mCoreKitPagedQuery.startInitQuery();
	```

4. Page refresh query:

	```java
	mCoreKitPagedQuery.startInitQuery();
	```

5. Load next page query:

	```java
	mCoreKitPagedQuery.startLoadMoreQuery();
	```

> Different from `CoreKitQuery`, a `CoreKitPagedQuery` object can only be in the service of a particular paging query.

#### 5. Implement mutation

1. New a `CoreKitMutation` object:

	```java
	CoreKitMutation coreKitMutation = new CoreKitMutation(this, DemoApplication.getInstance().abCoreKitClient());
	```

2. To initiate mutation, you only need to set the corresponding mutation object and callback object in the method. The results of mutation can be obtained in the callback object:

	```java
	coreKitMutation.mutation(mutation, new CoreKitResultListener<XXMutation.Data>() {
			@Override
			public void onSuccess(XXMutation.Data data) {
				// get the data
			}

			@Override
			public void onError(String errMsg) {
				// get the error message
			}

			@Override
			public void onComplete() {
				// mutation complete
			}
		});
	```

> A CoreKitMutation object can be used for processing more than one mutation object.

#### 6. Implement data subscription function

1. Open the socket switch when init the ABCoreClient :

	```java
	ABCoreKitClient.xxx
			.xxxx
			.setOpenSocket(true) // the socket switch
			.xxxx
			.build();
	```

2. New a `CoreKitSubscription` object:

	```java
	mCoreKitSubscription = new CoreKitSubscription<>(this, DemoApplication.getInstance().abCoreKitClientEth(), new NewBlockMinedSubscription(), NewBlockMinedSubscription.Data.class);
	```

3. Set ResultListener:

	```java
	mCoreKitSubscription.setResultListener(new CoreKitSubscriptionResultListener<NewBlockMinedSubscription.Data>() {
		@Override
		public void onSuccess(NewBlockMinedSubscription.Data data) {
			// get the new data
		}

		@Override
		public void onError(String errMsg) {
			// get the error message
		}
	});
	```

4. Set CoreKitSocketStatusCallBack:

	```java
	mCoreKitSubscription.setCoreKitSocketStatusCallBack(new CoreKitSocketStatusCallBack() {
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

> A CoreKitSubscription object can only serve a specific Subscription object.

#### 7. support HMAC Authentication

1. Go [https://console.arcblock.io](https://console.arcblock.io) registered an account, and in `Settings -> Security Settings` to create a set of `Access Key` and `Access Secret`.

2. Create a `ABCoreKitClient` and open the HMAC Authentication switch:

	```java
	mABCoreClientBtcWithHMAC = ABCoreKitClient.builder(this, CoreKitConfig.ApiType.API_TYPE_BTC)
		.setOpenOkHttpLog(true)
		.setEnableHMAC(true) // open HMAC Authentication
		.setDefaultResponseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
		.build();
	```
3. In app module `AndroidManifest.xml` configured in the application of `Access Key` and `Access Secret`:

	```xml
	<!-- For ArcBlock SDK-->
	<meta-data
		android:name="ArcBlock_Access_Key"
		android:value="<Your Access Key>" />
	<meta-data
		android:name="ArcBlock_Access_Secret"
		android:value="<Your Access Secret>" />
	```

#### 8. Other Settings

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
				utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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
	mABCoreClientBtc = ABCoreKitClient.builder(this, CoreKitConfig.ApiType.API_TYPE_BTC)
                    .addCustomTypeAdapter(CustomType.DATETIME, dateCustomTypeAdapter)
                    .setOpenOkHttpLog(true)
                    .setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_FIRST)
                    .build();
	```
	
	At the time of initialization, you can pass in custom `okHttpClient`, `CustomTypeAdapter`, `ResponseFetcher` parameters.

## License

ArcBlockSDK is available under the MIT license. See the [LICENSE file](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE) for more info.


