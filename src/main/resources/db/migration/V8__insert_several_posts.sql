insert into posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
           values (2, 1, 'NEW', 1, 1,  CAST('2020-08-30 09:43:06.427' AS DateTime), "Сортировка массива",
           "Пост про сортировку массива", 0);
insert into posts (is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
           values (1, 'NEW', 1, 1,  CAST('2020-08-30 14:43:06.427' AS DateTime), "Java синтаксис",
           "В этой статье вы узнаете про синтаксис языка Java", 0);
insert into posts (is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
           values (1, 'NEW', 1, 1,  CAST('2020-08-30 17:43:06.427' AS DateTime), "Графы",
           "В этой статье вы узнаете про устройство и работу с графами!", 0);