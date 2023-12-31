package com.dajungdagam.dg.service;

import com.dajungdagam.dg.domain.dto.WishlistDto;
import com.dajungdagam.dg.domain.entity.Post;
import com.dajungdagam.dg.domain.entity.User;
import com.dajungdagam.dg.domain.entity.Wishlist;
import com.dajungdagam.dg.repository.PostRepository;
import com.dajungdagam.dg.repository.UserJpaRepository;
import com.dajungdagam.dg.repository.WishListJpaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class WishlistService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private WishListJpaRepository wishlistRepository;

    // 빈 순환 참조 뜨니까 사용 안함
//    @Autowired
//    private TradePostService tradePostService;

    public Wishlist addWishlist(String kakaoName){
        User user = userRepository.findByKakaoName(kakaoName);

        Wishlist wishlist = new Wishlist(user);
        wishlistRepository.save(wishlist);

        log.info("찜목록 생성됨");

        return wishlist;
    }

    // TODO: 이미 찜목록되어 있으면, 알려야하나?
    @Transactional
    public Wishlist addPostToWishlist(WishlistDto wishlistDto){
        Wishlist wishlist = null;
        try {
            //log.info("wishlist 1 : " + wishlistDto.getKakaoName());
            User user = userRepository.findByKakaoName(wishlistDto.getKakaoName());
            //log.info("wishlist 2 : " + user.getKakaoName());
            wishlist = wishlistRepository.findByUser(user);
            //log.info("wish: " + wishlist.toString());

            int postCategory = wishlistDto.getPostCategory();
            Long postId = wishlistDto.getPostId();

            if(wishlist == null)    throw new Exception("wishlist is null");

            // 0: 공동구매인 경우
            if (postCategory == 0) {
                // 공동구매 아직 구현 X

            } else {
                Optional<Post> tradePostObj = postRepository.findById(postId);
                Post tradePost = tradePostObj.get();
                log.info("TradePost: "+ tradePost.toString());

                if(tradePost == null)    throw new Exception("wishlist is null");

                wishlist.addTradePost(tradePost);
                wishlistRepository.save(wishlist);
                // wishlistCount 증가
                tradePost.setWishlistCount(tradePost.getWishlistCount() + 1);

                log.info("wishlist의 tradeposts: " + wishlist.getTradePosts().toString());

            }
        } catch(Exception e){
            log.info(e.getMessage());
            return null;
        }
        return wishlist;
    }


    public Wishlist deletePostAtWishlist(String kakaoName, int postCategory, Long postId) {
        Wishlist wishlist= null;
        try {
            User user = userRepository.findByKakaoName(kakaoName);
            wishlist = wishlistRepository.findByUser(user);

            if(wishlist == null)    throw new Exception("wishlist is null");

            // 0: 공동구매인 경우
            if (postCategory == 0) {
                // 공동구매 아직 구현 X

            } else {
                Optional<Post> tradePostObj = postRepository.findById(postId);
                Post tradePost = tradePostObj.get();

                if(tradePost == null)    throw new Exception("wishlist is null");
                List<Post> tradePosts = wishlist.getTradePosts();

                Iterator<Post> iter = tradePosts.iterator();
                while(iter.hasNext()) {
                    Post target = iter.next();
                    if(target.getId().equals(postId)) {
                        // wishlistCount 감소
                        tradePost.setWishlistCount(tradePost.getWishlistCount() - 1);
                        iter.remove();
                    }
                }

//                tradePosts.stream().filter(post  -> {
//                    if(Objects.equals(post.getId(), postId)) return true;
//                    else return false;
//                })
//                        .toList().forEach(tradePosts::remove);

                wishlistRepository.save(wishlist);

                log.info("위시 리스트에서 " + postId + "번 게시글이 삭제됨.");
            }
        } catch(Exception e) {
            log.info(e.getMessage());
        }
        return wishlist;
    }

    public Wishlist getWishlistByUserId(int userId) {
        Optional<User> userObj = userRepository.findById(userId);
        User user = userObj.get();

        return wishlistRepository.findByUser(user);
    }

}
