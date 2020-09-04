insert into post_comments (id, post_id, user_id, time, text) values
          (2, 1, 1, CAST('2020-08-30 20:55:06.427' AS DateTime), "Очень познавательный пост!");
       insert into post_comments (post_id, user_id, time, text) values
                 (1, 1, CAST('2020-08-30 21:45:06.427' AS DateTime), "Побольше материала на эту тему!");