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
}

struct FeedList {
	1: list<Feed> feeds;
	2: i32 page_num,
	3: i32 page_count,
	4: i32 total_page_num,
}

service IPhotoService {
	// A test api.
	string hello(1:string name) throws (1:AException ae),

	// Get the feed list. Page start from 0.
	FeedList getFeedList(1:i32 page_num, 2:i32 page_count) throws (1:AException ae),
	
	// Upload a feed to server.
	Feed uploadFeed(1:Feed feed) throws (1:AException ae),
}
