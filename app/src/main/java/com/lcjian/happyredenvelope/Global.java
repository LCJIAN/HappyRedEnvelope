package com.lcjian.happyredenvelope;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class Global {

    public static DrawableTransitionOptions crossFade = DrawableTransitionOptions.withCrossFade();
    public static RequestOptions userAvatar = RequestOptions.placeholderOf(R.drawable.shape_user_no_avatar_bg).circleCrop();
    public static RequestOptions roomAvatar = RequestOptions.placeholderOf(R.drawable.shape_room_no_avatar_bg).transform(new RoundedCorners(4));
}
