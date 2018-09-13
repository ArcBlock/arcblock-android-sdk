# ArcBlock Android SDK

[![license](https://img.shields.io/badge/API-15+-green.svg?longCache=true&style=flat)](https://android-arsenal.com/api?level=15)  [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE)

[README of Chinese](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/README-CN.md)

> Developers need to have basic [GraphQL](https://graphql.org/) capabilities before accessing the ArcBlock Android SDK. We also provide a fully functional [ArcBlock OCAP Playground](https://ocap.arcblock.io/) where developers can use it to write and test the GraphQL statements they want.

## Absdkcorekit Library

Absdkcorekit Library is on the basis of the [apollo-android](https://github.com/apollographql/apollo-android) encapsulated ` Data ` layer core Library, we introduced the Android's latest ` Architecture Components `, among them the ` LiveData ` and ` ViewModel ` and ` Apollo - android Library ` combined with encapsulated into `CoreKitQueryViewModel`, `CoreKitPagedQueryViewModel`, `CoreKitSubscriptionViewModel`.

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

//......
dependencies {
  // x.x.x => release version
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

#### 3. Implement common query function

1. First, to customize a class inherited from the `CoreKitQuery` abstract class, you need to implement three parts:
	- **Constructor:** Implement a constructor that matches the current usage, depending on whether the Query is used in FragmentActivity or Fragment and whether the currently passed in is a custom ABCoreKitClient or the default ABCoreKitClient
	- **map(...) method:** The method for CoreKitBeanMapperInterface interface implementation, provide CoreKitQueryViewModel internal use, is used to return the Response into the final desired data format
	- **getQuery() method:** Initialize and return a current Query object to implement a concrete business Query

	Sample code:

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

	> Class naming a proposal to the corresponding `Query`, `Mutaition`, `Subscription` name plus `-Helper` end, such as the above AccountByAddressQuery corresponding AccountByAddressQueryHelper

2. The second step, create a `xxxHelper` query help the object of the class, and set the Observe object

	- Create a `xxxHelper` class object:
	
		```java
		AccountByAddressQueryHelper accountByAddressQueryHelper = new AccountByAddressQueryHelper(this, this, DemoApplication.getInstance().abCoreKitClientBtc());
		```

		As mentioned above, there are four different implementations to choose from.

	- Set the Observe object:
	
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
	
#### 4. Implement paged Query function

1. First, to customize a class inherited from the `CoreKitPagedQuery` abstract class, you need to implement five parts:
	- **Constructor:** Implement a constructor that matches the current usage, depending on whether the paged query is used in FragmentActivity or Fragment and whether the currently passed in is a custom ABCoreKitClient or the default ABCoreKitClient
	- **map(...) method:** The method for CoreKitBeanMapperInterface interface implementation, provide CoreKitPagedQueryViewModel internal use, is used to return the Response into the final desired data format
		
		> The difference here is that the map(...) approach, the need to manually set `setHasMore(boolean hasMore)` and `setCursor(String cursor)`, these two parameters is the basis of the underlying determine whether paging request
		
	- **getInitialQuery() method:** Initialize and return an initial Query object for a paged query
	- **getLoadMoreQuery() method:** Initialize and return a Query object for more queries
	- **getRefreshQuery() method:** Initialize and return a Query object for the paging Query refresh Query, which is generally the same as the Query object returned by getInitialQuery()

	Sample code:

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

	> Class naming a proposal to the corresponding `Query`, `Mutaition`, `Subscription` concrete class name plus `-Helper` end, such as the above BlocksByHeightQuery corresponding BlocksByHeightQueryHelper

2. The second step, create a `xxxHelper` query help the object of the class, and set the Observe object, request and get the data

	- Create a `xxxHelper` class object:
	
		```java
		mBlocksByHeightQueryHelper = new BlocksByHeightQueryHelper(this, this, DemoApplication.getInstance().abCoreKitClientBtc());
		```

		As mentioned above, there are four different implementations to choose from.
	
	- Set the Observe object:

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

	- Call the refresh method to refresh

		```java
		mBlocksByHeightQueryHelper.refresh();
		```

	- Call the loadMore method to load the next page of data

		```java
		mBlocksByHeightQueryHelper.loadMore();
		```

#### 5. Implement data subscription function

1. Open the socket switch when init the ABCoreClient :

	```java
	ABCoreKitClient.xxx
			.xxxx
			.setOpenSocket(true) // the socket switch
			.xxxx
			.build();
	```

2. The second step, customize a class to inherit from the `CoreKitSubscription` abstract class, and you need to implement three sections:

	- **Constructor:** Implement a constructor that matches the current usage, depending on whether the subscription is used in FragmentActivity or Fragment and whether the currently passed in is a custom ABCoreKitClient or the default ABCoreKitClient
	- **getSubscription() method:** Initialize and return a Subscription object
	- **getResultDataClass() method:** Return the desired Data type of the Class, eventually for CoreKitSubscriptionViewModel in json parsing

	Sample code:

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

	> Class naming a proposal to the corresponding `Query`, `Mutaition`, `Subscription` concrete class name plus `-Helper` end, such as the above NewBlockMinedSubscription corresponding NewBlockMinedSubscriptionHelper

3. The third step, create a `xxxHelper` class object and set CoreKitSubCallBack, CoreKitSocketStatusCallBack
    
	- Create a `xxxHelper` class object:

		```java
		mNewBlockMinedSubscriptionHelper = new NewBlockMinedSubscriptionHelper(this, DemoApplication.getInstance().abCoreKitClientEth());
		```

		As mentioned above, there are four different implementations to choose from.

	- Set the CoreKitSubCallBack

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
	- Set the CoreKitSocketStatusCallBack

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
                    .setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
                    .build();
	```
	
	At the time of initialization, you can pass in custom `okHttpClient`, `CustomTypeAdapter`, `ResponseFetcher` parameters.

## License

ArcBlockSDK is available under the MIT license. See the [LICENSE file](https://github.com/ArcBlock/arcblock-android-sdk/blob/master/LICENSE) for more info.


