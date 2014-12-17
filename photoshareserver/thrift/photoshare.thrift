namespace java com.example.photoshare.thrift

// Show error message to front end.
exception AException {
	1: string message
}

struct Feed {
	// Generated in server
	1: string feed_id,
	// Set in client
	2: string user_name,
	3: string photo_url,
	4: string feed_desc,
	5: i64 timestamp,
}

struct FeedUploadReq {
	1: string user_name,
	2: binary photo_data,
	3: string feed_desc,
}

struct FeedList {
	1: list<Feed> feeds;
	2: bool has_more_data;
}

service IPhotoService {
	// A test api.
	string hello(1:string name) throws (1:AException ae),

	// Get the feed list. Page start from 0.
	FeedList getFeedList(1:string last_feed_id, 2:i32 page_count) throws (1:AException ae),

	// Upload a feed to server.
	Feed uploadFeed(1:FeedUploadReq feed) throws (1:AException ae),
}
