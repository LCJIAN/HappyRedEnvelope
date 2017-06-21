package com.lcjian.happyredenvelope.data.network;


import com.lcjian.happyredenvelope.data.entity.Message;
import com.lcjian.happyredenvelope.data.entity.RedEnvelope;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.happyredenvelope.data.entity.User;
import com.lcjian.happyredenvelope.data.entity.Vip;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RedEnvelopeService {

    /**
     * 用户登陆
     */
    @GET("user/user/register")
    Observable<ResponseData<User>> register(@Field("openid") String openId,
                                            @Field("nickname") String nickName,
                                            @Field("headimg") String avatar,
                                            @Field("country") String country,
                                            @Field("province") String province,
                                            @Field("city") String city,
                                            @Field("sex") int sex);

    /**
     * 用户是否VIP
     */
    @GET("user/user/isvip")
    Observable<ResponseData<Vip>> isVip(@Field("userid") long userId);

    /**
     * 获取普通房间列表
     */
    @GET("room/getnormalrooms")
    Observable<ResponseData<List<Room>>> getNormalRooms(@Field("page") int pageNumber,
                                                        @Field("pagesize") int pageSize);

    /**
     * 获取VIP房间列表
     */
    @GET("room/getviprooms")
    Observable<ResponseData<List<Room>>> getVipRooms(@Field("page") int pageNumber,
                                                        @Field("pagesize") int pageSize);

    /**
     * 搜索房间（按ID号）
     */
    @GET("room/searchroom")
    Observable<ResponseData<List<Room>>> searchRoom(@Field("roomid") long roomId);

    /**
     * VIP创建房间
     */
    @GET("room/addviproom")
    Observable<ResponseData<Long>> createVipRoom(@Field("uid") long userId,
                                                       @Field("name") String name,
                                                       @Field("desc") String desc,
                                                       @Field("icon") String icon);

    /**
     * 进入房间
     */
    @GET("room/joinroom")
    Observable<ResponseData<Long>> joinRoom(@Field("uid") long userId,
                                                 @Field("roomid") long roomid);

    /**
     * 退出房间
     */
    @GET("room/joinroom")
    Observable<ResponseData<String>> exitRoom(@Field("uid") long userId,
                                            @Field("roomid") long roomid);

    /**
     * 退获取增量消息（每5S获取一次）
     */
    @GET("roommsg/getaddmsg")
    Observable<ResponseData<List<Message>>> getAddedMsg(@Field("uid") long userId,
                                                     @Field("roomid") long roomid);

    /**
     * 打开红包
     */
    @GET("hongbao/openhongbao")
    Observable<ResponseData<Long>> openRedEnvelope(@Field("uid") long userId,
                                                        @Field("msgid") long msgId);

    /**
     * 获取红包记录
     */
    @GET("hongbao/gethongbaohistory")
    Observable<ResponseData<List<RedEnvelope>>> getRedEnvelopeHistories(@Field("uid") long userId,
                                                                        @Field("page") int pageNumber,
                                                                        @Field("pagesize") int pageSize);
}
