package com.dajungdagam.dg.Controller;

import com.dajungdagam.dg.domain.dto.UserResponseDto;
import com.dajungdagam.dg.domain.dto.WishlistDto;
import com.dajungdagam.dg.domain.entity.Wishlist;
import com.dajungdagam.dg.service.UserService;
import com.dajungdagam.dg.service.WishlistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserService userService;

    // 찜하기
    @PostMapping("/wishlist")
    public ResponseEntity<String> likes(Authentication authentication, @RequestBody WishlistDto wishlistDto) {

        try{
            if(authentication == null)
                throw new Exception("authentication is null. non user Info");

            // Authentication 유저
            String authKakaoName = authentication.getName();
            UserResponseDto authUserResponseDto = userService.findByUserKakaoNickName(authKakaoName);

            // wishlistDto 유저
            String wishKakaoName = wishlistDto.getKakaoName();
            UserResponseDto wishUserResponseDto =  userService.findByUserKakaoNickName(wishKakaoName);

            boolean sameCheck = userService.isSameUser(authUserResponseDto, wishUserResponseDto);
            if(!sameCheck) throw new Exception("권한이 없습니다.");


            Wishlist wishlist = wishlistService.addPostToWishlist(wishlistDto);
            if(wishlist == null)    throw new Exception("찜하기 실패");

            return ResponseEntity.ok().body("찜 완료");
        } catch(Exception e){

            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 찜 취소하기
    @DeleteMapping("/wishlist")
    public ResponseEntity<Wishlist> deletePost(Authentication authentication,
                                               @RequestParam int postCategory, @RequestParam Long postId){
        Wishlist wishlist = null;
        try{
            if(authentication == null)  throw new Exception("authentication is null");

            String kakaoName = authentication.getName();

            wishlist = wishlistService.deletePostAtWishlist(kakaoName, postCategory, postId);

            log.info("찜목록 게시글 삭제됨.");
            log.info(wishlist.toString());


        } catch (Exception e){
            log.error(e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        log.info("wishlist: " + wishlist.toString());

        return new ResponseEntity<>(wishlist, headers, HttpStatus.OK);

    }

    @PostMapping("/wishlist/{userId}")
    public ResponseEntity<Wishlist> getWishlistByUserId(Authentication authentication, @PathVariable("userId") int userId){
        try{
            if(authentication == null)
                throw new Exception("authentication is null. non user Info");

            String kakaoName = authentication.getName();
            UserResponseDto userResponseDto = userService.findByUserKakaoNickName(kakaoName);

            if(!UserService.isSameUser(userId, userResponseDto))
                throw new Exception("권한이 없습니다.");

            Wishlist wishlist = wishlistService.getWishlistByUserId(userId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

            log.info("wishlist: " + wishlist.toString());

            return new ResponseEntity<>(wishlist, headers, HttpStatus.OK);
        } catch(Exception e){

            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }





    }
}
