package main.service;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import main.api.responseAndAnswers.comment.CommentErrors;
import main.api.responseAndAnswers.comment.PutCommentAnswer;
import main.api.responseAndAnswers.comment.PutCommentRequest;
import main.api.responseAndAnswers.post.*;
import main.api.responseAndAnswers.user.StatisticAnswer;
import main.model.*;
import main.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PostVotesRepository postVotesRepository;;
    private final CalendarService calendarService;
    private final SettingsService settingsService;

    public ResponseEntity<PostsResponse> postsForMainPage(String mode, int offset, int limit){
        Pageable pageable = PageRequest.of(offset/limit, limit);
        Page<Post> posts = null;
        PostsResponse postsResponse;
        LocalDateTime currentTime = LocalDateTime.now();
        switch (mode) {
            case "recent": {
                posts = postRepository.findRecentPosts(currentTime, pageable);
            }
                break;
            case "early": {
                posts = postRepository.findOldPosts(currentTime, pageable);
            }
                break;
            case "popular":{
                posts = postRepository.findMostCommentedPosts(currentTime, pageable);
            }
                break;
            case "best": {
                posts = postRepository.findBestPosts(currentTime, pageable);
            }
                break;
        }
        postsResponse = new PostsResponse(posts);
        return ResponseEntity.ok().body(postsResponse);
    }

    public ResponseEntity<PostsResponse> searchPosts(int offset, int limit, String query) {
        Pageable pageable = PageRequest.of(offset, limit);
        if (query == null) {
            return ResponseEntity.ok().body(findAllWithPageable(pageable));
        } else {
            Page<Post> posts = postRepository.searchPostsByQuery(query, LocalDateTime.now(), pageable);
            PostsResponse response = posts == null ? PostsResponse.getEmptyResonse() : new PostsResponse(posts);
            return ResponseEntity.ok().body(response);
        }
    }

    public PostsResponse findAllWithPageable(Pageable pageable) {
        Page<Post> posts = postRepository.finAllWithPageable(LocalDateTime.now(), pageable);
        return new PostsResponse(posts);
    }

    public ResponseEntity<PostsResponse> searchPostsByDate(int offset, int limit, String date) {
        String dateComponents[] = date.split("-");
        Pageable pageable = PageRequest.of(offset, limit);
        LocalDateTime startTime = calendarService.getStartTime(dateComponents);
        LocalDateTime endTime = calendarService.getEndTime(dateComponents);
        Page<Post> posts = postRepository.findAllByDate(startTime, endTime, pageable);
        return assemblingGroupOfPosts(posts);
    }

    public ResponseEntity<PostsResponse> getPostByTag(int offset, int limit, String tagName) {
        int tagId = tagRepository.getTagByName(tagName).get().getId();
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Post> posts = tag2PostRepository.findsPosts(tagId, LocalDateTime.now(), pageable);
        return assemblingGroupOfPosts(posts);
    }

    public ResponseEntity<PostsResponse> getPostForModeration(int offset, int limit, String status, Principal principal) {
        Page<Post> posts;
        User user = userRepository.findByEmail(principal.getName()).get();
        Pageable pageable = PageRequest.of(offset, limit);
        ModerationStatus moderationStatus = ModerationStatus.valueOf(status.toUpperCase());
        switch (status) {
            case "new":
                posts = postRepository.findNew(pageable);
                break;
            default:
                posts = postRepository.findMyModeration(user.getId(), moderationStatus, pageable);
        }
        return assemblingGroupOfPosts(posts);
    }

    public ResponseEntity<PostsResponse> getMyPosts(int offset, int limit, String status, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        status = status.equals("pending") ? "NEW" : status.equals("declined") ? "DECLINED" : status.equals("published")? "ACCEPTED" : "INACTIVE";
        Page<Post> posts;
        Pageable pageable = PageRequest.of(offset, limit);
        switch (status) {
            case "INACTIVE":
                posts = postRepository.myPostsInactive(user.getId(), pageable);
                break;
            default:
                posts = postRepository.myPostsWithStatus(user.getId(), ModerationStatus.valueOf(status), pageable);
        }
        return assemblingGroupOfPosts(posts);
    }

    public ResponseEntity<GetPostResponse> findById(int id, Principal principal) {
        Post firstPost = postRepository.findByIdAndActive(id, LocalDateTime.now());
        if ( (firstPost == null) && (principal != null)) {
            try {
                firstPost = findAlternativePost(principal, id);
            } catch (Exception exception){
                return ResponseEntity.notFound().build();
            }
        } else if(firstPost == null){
            return ResponseEntity.notFound().build();
        }
        Post getPost = firstPost;
        changeNumberOfViews(principal, getPost);
        return ResponseEntity.ok().body(formingPostForAnswer(id, getPost));
    }

    private Post findAlternativePost(Principal principal, int id) throws NotFoundException, AccessDeniedException {
        User user = userRepository.findByEmail(principal.getName()).get();
        Optional<Post> secondPost = postRepository.findById(id);
        if(secondPost.isPresent()){
            Post post = secondPost.get();
            if((post.getUser().getId() == user.getId()) || (user.getIsModerator() == 1)) {
                return secondPost.get();
            } else {
                throw new AccessDeniedException("Пользователь не имеет доступа к этому посту");
            }
        } else {
            throw new NotFoundException("Пост не найден");
        }
    }

    private void changeNumberOfViews(Principal principal, Post post){
        if((principal != null)){
            User user = userRepository.findByEmail(principal.getName()).get();
            if(!((user.getIsModerator() == 1) || (user.getId() == post.getUser().getId()))){
                post.setViewCount(post.getViewCount() + 1);
                postRepository.save(post);
            }
        } else {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
    }

    public ResponseEntity<PutPostResponse> putOrEditPost(Principal principal, PostAdd postForAdd){
        return putOrEditPost(principal, postForAdd, -1);
    }

    public ResponseEntity<PutPostResponse> putOrEditPost(Principal principal, PostAdd postForAdd, int id) {
        PutPostResponse postResponse = findErrors(postForAdd);
        if (postResponse.isResult()) {
            Post post = makePost(principal, postForAdd, id);
            String[] tags = postForAdd.getTags();
            post = postRepository.save(post);
            checkAndAddTags(tags, post);
        }
        return ResponseEntity.ok().body(postResponse);
    }

    private Post makePost(Principal principal, @RequestBody PostAdd postForAdd, int idGot){
        User user = userRepository.findByEmail(principal.getName()).get();
        long id = idGot != -1? idGot : postRepository.count() + 1;
        Post post = new Post();
        post.setId((int) id);
        Timestamp stamp = new Timestamp(postForAdd.getTimestamp() * 1000);
        LocalDateTime currentTime = LocalDateTime.now();
        post.setUser(user);
        post.setTime(currentTime.isAfter(stamp.toLocalDateTime()) ? currentTime : stamp.toLocalDateTime());
        post.setIsActive(postForAdd.getActive());
        post.setTitle(postForAdd.getTitle());
        post.setText(postForAdd.getText());
        post.setModerationStatus(settingsService.getPostModerationMode().getValue().equals("YES") ?
                ModerationStatus.NEW : ModerationStatus.ACCEPTED);
        post.setDislikeVotes(new ArrayList<PostVote>());
        post.setLikeVotes(new ArrayList<PostVote>());
        post.setPostComments(new ArrayList<PostComment>());
        return post;
    }

    private GetPostResponse formingPostForAnswer(int id, Post getPost){
        List<TagToPost> tags = tag2PostRepository.findPostById(id);
        List<PostComment> postComments = postCommentRepository.findByPostId(id);
        GetPostResponse response = new GetPostResponse();
        response.formatingAnswer(getPost, postComments, tags);
        return response;
    }

    public ResponseEntity<PostsResponse> assemblingGroupOfPosts(Page<Post> posts){
        switch((int)posts.getTotalElements()) {
            case 0:
                return ResponseEntity.ok().body(PostsResponse.getEmptyResonse());
            default: {
                return ResponseEntity.ok().body(new PostsResponse(posts));
            }
        }
    }

    public PutPostResponse findErrors(PostAdd postForAdd) {
        PostErrors errors = getErrors(postForAdd);
        return errors != null ? new PutPostResponse(false, errors) : new PutPostResponse(true, null);
    }

    public PostErrors getErrors(PostAdd postForAdd) {
        PostErrors errors = new PostErrors();
        String title = postForAdd.getTitle();
        String text = postForAdd.getText();
        errorInTitle(errors, title);
        errorInText(errors, text);
        boolean areErrors = errors.checkErrors(errors);
        return areErrors ? errors : null;
    }

    private void errorInTitle(PostErrors errors, String title) {
        if (title.equals("")) {
            errors.setTitle("Заголовок не установлен");
        }
    }

    private void errorInText(PostErrors errors, String text) {
        if (text.equals(null)) {
            errors.setText("Текс публикации не установлен");
        } else if (text.length() < 10) {
            errors.setText("Текст публикации слишком короткий");
        }
    }

    private void checkAndAddTags(String[] tags, Post post) {
        deleteTags(tags, post);
        for (String tag : tags) {
            if(!alreadyExist(post, tag)){
                addTag(tag, post);
            }
        }
    }

    private boolean alreadyExist(Post post, String tag){
        TagToPost tagToPost = tag2PostRepository.findByTagAndPost(post.getId(), tag);
        return tagToPost == null? false : true;
    }

    private void addTag(String tag, Post post){
        if (tagRepository.getTagByName(tag).isEmpty()) {
            Tag tag2 = new Tag();
            tag2.setName(tag);
            tagRepository.save(tag2);

        }
        TagToPost tagToPost = new TagToPost();
        tagToPost.setId((int) (tag2PostRepository.count() + 1));
        tagToPost.setPost(post);
        tagToPost.setTag(tagRepository.getTagByName(tag).get());
        tag2PostRepository.save(tagToPost);
    }

    private void deleteTags(String[] tags, Post post){
        zeroCase(tags, post);
        moreThanOne(tags, post);
    }

    private void zeroCase(String[] tags, Post post){
        if(tags.length == 0){
            List<TagToPost> tagToPosts = tag2PostRepository.findPostById(post.getId());
            tagToPosts.forEach(t -> tag2PostRepository.delete(t));
        }
    }

    private void moreThanOne(String[] tags, Post post){
        List<TagToPost> tagToPosts = tag2PostRepository.findPostById(post.getId());
        Arrays.sort(tags);
        tagToPosts.forEach(tagToPost -> {
            int id = Arrays.binarySearch(tags, tagToPost.getTag().getName());
            if(id < 0){
                tag2PostRepository.delete(tagToPost);
            }
        });
    }

    public ResponseEntity<ModerationAnswer> moderation(Principal principal, ModerationRequest request){
        User moderator = userRepository.findByEmail(principal.getName()).get();
        Post post = postRepository.findById(request.getPostId()).get();
        post.setModerator(moderator);
        String decision = request.getDecision();
        switch (decision){
            case "accept":{
                post.setModerationStatus(ModerationStatus.ACCEPTED);
            }
            break;
            case "decline":{
                post.setModerationStatus(ModerationStatus.DECLINED);
            }
            break;
        }
        postRepository.save(post);
        return ResponseEntity.ok().body(new ModerationAnswer(true));
    }

    public ResponseEntity<PutCommentAnswer> addComment(PutCommentRequest request, Principal principal){
        Optional<Post> post = postRepository.findById(request.getPostId());
        Optional<User> user = userRepository.findByEmail(principal.getName());
        Optional<PostComment> parent = postCommentRepository.findById(request.getParentId());
        if((post.isEmpty()) || (user.isEmpty()) || ((parent.isEmpty()) && (request.getParentId() != 0))){
            return  ResponseEntity.badRequest().build();
        }
        if(request.getText().equals("") || request.getText().length() < 10){
            PutCommentAnswer answer = new PutCommentAnswer(null, false, new CommentErrors("Текст комментария не задан или слишком короткий"));
            return ResponseEntity.ok().body(answer);
        }
        return saveCommentInfo(post, user, parent, request);
    }

    private ResponseEntity<PutCommentAnswer> saveCommentInfo(Optional<Post> post, Optional<User> user, Optional<PostComment> parent, PutCommentRequest request){
        Post postReal = post.get();
        User userReal = user.get();
        PostComment parentReal = parent.isPresent()? parent.get() : null;
        PostComment comment = new PostComment();
        comment.setParent(parentReal);
        comment.setPost(postReal);
        comment.setUser(userReal);
        comment.setText(request.getText());
        comment.setTime(LocalDateTime.now());
        postCommentRepository.save(comment);
        PutCommentAnswer answer = new PutCommentAnswer(comment.getId(), null, null);
        return ResponseEntity.ok().body(answer);
    }

    public ResponseEntity<StatisticAnswer> myStatistic(Principal principal){
        User user = userRepository.findByEmail(principal.getName()).get();
        List<Post> posts = postRepository.myPosts(user.getId(), "ACCEPTED");
        return getStatistic(posts);
    }

    public ResponseEntity<StatisticAnswer> allStatistic(Principal principal){
        if(unauthorizedError(principal)){
            return ResponseEntity.status(401).build();
        }
        List<Post> posts = postRepository.findAllActive(LocalDateTime.now());
        return getStatistic(posts);
    }

    private boolean unauthorizedError(Principal principal){
        String globalStatisticSetting = settingsService.getStatisticsMode().getValue();
        if(principal == null) return nullCase(globalStatisticSetting);
        return defaultCase(principal, globalStatisticSetting);
    }

    private boolean nullCase(String globalStatisticSetting){
        return globalStatisticSetting.equals("NO");
    }

    private boolean defaultCase(Principal principal, String globalStatisticSetting) {
        Optional<User> user = userRepository.findByEmail(principal.getName());
        if(user.isEmpty()){
            return userIsUnauthorized(globalStatisticSetting);
        } else {
            return userIsAuthorized(user, globalStatisticSetting);
        }
    }

    private boolean userIsAuthorized(Optional<User> optionalUser, String globalStatisticSetting){
        User user = optionalUser.get();
        return (user.getIsModerator() != 1 && globalStatisticSetting.equals("NO"));
    }

    private boolean userIsUnauthorized(String globalStatisticSetting){
        return globalStatisticSetting.equals("NO");
    }

    private ResponseEntity<StatisticAnswer> getStatistic(List<Post> posts){
        StatisticAnswer answer = new StatisticAnswer();
        answer.setPostsCount(posts.size());
        final long[] likeCount = {0};
        final long[] dislikesCount = {0};
        final long[] viewsCount = {0};
        long firstPublication = 0;
        posts.forEach(p -> {
            likeCount[0] += p.getLikeVotes().size();
            dislikesCount[0] += p.getDislikeVotes().size();
            viewsCount[0] += p.getViewCount();
        });
        posts.sort(Comparator.comparing(Post::getTime));
        Timestamp timestamp = Timestamp.valueOf(posts.get(0).getTime());
        firstPublication = timestamp.getTime();
        answer.setLikesCount(likeCount[0]);
        answer.setDislikesCount(dislikesCount[0]);
        answer.setViewsCount(viewsCount[0]);
        answer.setFirstPublication(firstPublication/1000);
        return ResponseEntity.ok().body(answer);
    }

    public ResponseEntity<PutPostResponse> putVote(Principal principal, int id, int vote){
        User user = userRepository.findByEmail(principal.getName()).get();
        Post post = postRepository.findById(id).get();
        Optional<PostVote> optionalPostVote = postVotesRepository.byUserAndPostId(user.getId(), post.getId());
        if((optionalPostVote.isPresent())){
            return ifVotePresent(optionalPostVote, vote, post);
        }
        PostVote postVote = new PostVote();
        postVote.setId((int) (postVotesRepository.count() + 1));
        postVote.setPost(post);
        postVote.setUser(user);
        postVote.setTime(LocalDateTime.now());
        postVote.setValue((byte) vote);
        postVotesRepository.save(postVote);
        return ResponseEntity.ok().body(new PutPostResponse(true, null));
    }

    private ResponseEntity<PutPostResponse> ifVotePresent(Optional<PostVote> optionalPostVote, int vote, Post post){
        PostVote postVote = optionalPostVote.get();
        if(postVote.getValue() == vote){
            return ResponseEntity.ok().body(new PutPostResponse(false, null));
        }
        if(vote == 1){
            post.getDislikeVotes().remove(postVote);
        } else post.getLikeVotes().remove(postVote);
        postVotesRepository.delete(postVote);
        postVote.setValue((byte) (postVote.getValue() * -1));
        postVote.setTime(LocalDateTime.now());
        postVotesRepository.save(postVote);
        return ResponseEntity.ok().body(new PutPostResponse(true, null));
    }


}
