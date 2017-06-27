package com.lcjian.happyredenvelope.data.network;

import com.lcjian.happyredenvelope.data.entity.Banner;
import com.lcjian.happyredenvelope.data.entity.Billboard;
import com.lcjian.happyredenvelope.data.entity.Explore;
import com.lcjian.happyredenvelope.data.entity.Message;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.RedEnvelope;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.happyredenvelope.data.entity.User;
import com.lcjian.happyredenvelope.data.entity.VipPrivilege;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface RedEnvelopeService {

    /**
     * 用户登陆
     */
    @FormUrlEncoded
    @POST("user/user/register")
    Observable<ResponseData<User>> register(@Field("openid") String openId,
                                            @Field("nickname") String nickName,
                                            @Field("headimg") String avatar,
                                            @Field("country") String country,
                                            @Field("province") String province,
                                            @Field("city") String city,
                                            @Field("sex") String sex);

    /**
     * 用户登陆
     */
    @FormUrlEncoded
    @POST("user/user/register")
    Observable<ResponseData<UserSummary>> getUserSummary(@Field("userid") long userId);

    /**
     * 获取普通房间列表
     */
    @FormUrlEncoded
    @POST("room/getnormalrooms")
    Observable<ResponseData<PageResult<Room>>> getNormalRooms(@Field("page") int pageNumber,
                                                              @Field("pagesize") int pageSize);

    /**
     * 获取VIP房间列表
     */
    @FormUrlEncoded
    @POST("room/getviprooms")
    Observable<ResponseData<PageResult<Room>>> getVipRooms(@Field("page") int pageNumber,
                                                           @Field("pagesize") int pageSize);

    /**
     * 搜索房间（按ID号）
     */
    @FormUrlEncoded
    @POST("room/searchroom")
    Observable<ResponseData<List<Room>>> searchRoom(@Field("roomid") long roomId);

    /**
     * VIP创建房间
     */
    @FormUrlEncoded
    @POST("room/addviproom")
    Observable<ResponseData<Long>> createVipRoom(@Field("uid") long userId,
                                                 @Field("name") String name,
                                                 @Field("desc") String desc,
                                                 @Field("icon") String icon);

    /**
     * 进入房间
     */
    @FormUrlEncoded
    @POST("room/joinroom")
    Observable<ResponseData<Long>> joinRoom(@Field("uid") long userId,
                                            @Field("roomid") long roomid);

    /**
     * 退出房间
     */
    @FormUrlEncoded
    @POST("room/joinroom")
    Observable<ResponseData<String>> exitRoom(@Field("uid") long userId,
                                              @Field("roomid") long roomid);

    /**
     * 退获取增量消息（每5S获取一次）
     */
    @FormUrlEncoded
    @POST("roommsg/getaddmsg")
    Observable<ResponseData<List<Message>>> getAddedMsg(@Field("uid") long userId,
                                                        @Field("roomid") long roomid);

    /**
     * 打开红包
     */
    @FormUrlEncoded
    @POST("hongbao/openhongbao")
    Observable<ResponseData<Long>> openRedEnvelope(@Field("uid") long userId,
                                                   @Field("msgid") long msgId);

    /**
     * 获取幸运手气榜
     *
     * @param type 1-日榜  2-周榜  3-月榜
     */
    @FormUrlEncoded
    @POST("rank/getluckrank")
    Observable<ResponseData<PageResult<Billboard>>> getBillboard(@Field("uid") long userId,
                                                                 @Field("type") int type,
                                                                 @Field("page") int pageNumber,
                                                                 @Field("pagesize") int pageSize);

    /**
     * 获取会员特权
     */
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("user/vip/getright")
    Observable<ResponseData<List<VipPrivilege>>> getVipPrivileges();

    /**
     * 获取发现参数
     */
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("user/found/getparams")
    Observable<ResponseData<Explore>> getExplore();

    /**
     * 获取发现参数
     */
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("user/found/getbanner")
    Observable<ResponseData<List<Banner>>> getBanners();

    /**
     * 获取红包记录
     */
    @FormUrlEncoded
    @POST("hongbao/gethongbaohistory")
    Observable<ResponseData<PageResult<RedEnvelope>>> getRedEnvHistories(@Field("uid") long userId,
                                                                         @Field("page") int pageNumber,
                                                                         @Field("pagesize") int pageSize);
}
