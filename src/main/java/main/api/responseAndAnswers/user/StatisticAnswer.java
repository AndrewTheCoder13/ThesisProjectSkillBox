package main.api.responseAndAnswers.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticAnswer {
    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private long viewsCount;
    private long firstPublication;
}
