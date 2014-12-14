namespace java com.example.photoshare.api

// Show error message to front end.
exception AException {
	1: string message
}

struct User {
	1: string id,
	2: string name,
}

struct Feed {
	// Generated in server
	1: string feed_id,
	// Set in client
	2: string user_name,
	3: string photo_url,
	4: string feed_desc,
}

service PhotoService {
	// A test api.
	string hello(1:string name) throws (1:AException ae),

	// Get the feed list. Page start from 0.
	list<Feed> getFeedList(1:i32 page, i32 page_count) throws (1:AException ae),
	
	// Upload a feed to server.
	Feed uploadFeed(Feed feed) throws (1:AException ae),
}