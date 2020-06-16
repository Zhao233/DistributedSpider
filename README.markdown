# 分布式爬虫	

## spider	
信息展示系统	

## spider_weibo_comment	
爬取微博评论	    
1. 从kafka获取正文id后，进行指定正文的评论爬取
2. 只爬取微博的一级评论（直接对正文的评论）

## spider_weibo_content
爬取微博正文	
1. 利用zookeeper，存取全局page，每个独立的程序再更新自己需要爬取的页面时，争取zookeeper端的锁，保证每个独立程序的page都是全局唯一。
2. 爬取一条微博正文后，将正文id存取至kafka，交由评论爬取程序爬取