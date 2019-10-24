# z5-sdk-java-core-http

An implementation of the Z5 Java SDK using an embedded HttpClient

# Getting started

In order to use this SDK, you require both a Zone5 server endpoint and the ability to obtain a user oauth bearer token.

Once you have these, you can initialise the Z5HttpClient.java, allowing you to make authenticated API calls.

```
Z5HttpClient.get().setToken(token);
Z5HttpClient.get().setHostname(server);

# Enable debug logging of the requests and responses
Z5HttpClient.get().setDebug(true);
```

You can also set the server and token in a local user configuration file, allowing you to run unit tests which extend the BaseTest.java class. 

Create a text file in ~/z5.env with the following entries;

```
server = staging.todaysplan.com.au
token = <my token>
```

Within the Z5HttpClient is an Apache HttpClient - you can pass in your own HttpClient or use a default instance. 

Examples of using this SDK can be found in the ~/src-test directory.

When using the various API helper classes, the requests provide both a Future<> and a callback/handler. You can process the server response either via the Future or via the handler.

```
@Test
public void testSearchForLast10Activities() throws Exception {
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		
		// Just request some summary fields
		search.setFields(Arrays.asList("name", "distance", "training", "avgWatts", "avgBpm", "lat1", "lon1", "startTs", "locality", "peak3secWatts", "headunit.name", "aap.avgWatts"));
		
		// We only want completed rides with files
		search.getCriteria().setIsNotNull(Arrays.asList("fileId"));
		
		// Order by ride start time desc
		search.getCriteria().setOrder(Arrays.asList(new Order("startTs", com.zone5ventures.core.enums.Order.desc)));
		
		// Limit to rides with a startTs <= now - ie avoid dodgy files which might have a timestamp in the future!
		search.getCriteria().setToTs(System.currentTimeMillis());
		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 10).get().getResult();
		
```
