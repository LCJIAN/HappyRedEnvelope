package com.lcjian.happyredenvelope.data.network;

import com.lcjian.happyredenvelope.data.entity.AppLinks;
import com.lcjian.happyredenvelope.data.entity.Banner;
import com.lcjian.happyredenvelope.data.entity.Billboard;
import com.lcjian.happyredenvelope.data.entity.Explore;
import com.lcjian.happyredenvelope.data.entity.FreeLuckCard;
import com.lcjian.happyredenvelope.data.entity.Goods;
import com.lcjian.happyredenvelope.data.entity.GoodsHistory;
import com.lcjian.happyredenvelope.data.entity.LeftTimeInfo;
import com.lcjian.happyredenvelope.data.entity.LuckCardCombo;
import com.lcjian.happyredenvelope.data.entity.LuckCardSummary;
import com.lcjian.happyredenvelope.data.entity.Message;
import com.lcjian.happyredenvelope.data.entity.OpenResult;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.RedEnvHot;
import com.lcjian.happyredenvelope.data.entity.RedEnvelope;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.happyredenvelope.data.entity.RoomAndCreator;
import com.lcjian.happyredenvelope.data.entity.RoomBillboard;
import com.lcjian.happyredenvelope.data.entity.RoomHistory;
import com.lcjian.happyredenvelope.data.entity.RoomIdInfo;
import com.lcjian.happyredenvelope.data.entity.SnatchingDetail;
import com.lcjian.happyredenvelope.data.entity.User;
import com.lcjian.happyredenvelope.data.entity.UserSummary;
import com.lcjian.happyredenvelope.data.entity.Users;
import com.lcjian.happyredenvelope.data.entity.Video;
import com.lcjian.happyredenvelope.data.entity.VideoHistory;
import com.lcjian.happyredenvelope.data.entity.VipCombo;
import com.lcjian.happyredenvelope.data.entity.VipInfo;
import com.lcjian.happyredenvelope.data.entity.VipPrivilege;
import com.lcjian.happyredenvelope.data.entity.WeChatPayOrder;
import com.lcjian.happyredenvelope.data.entity.Withdrawal;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface RedEnvelopeService {

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
    @POST("found/getparams")
    Observable<ResponseData<Explore>> getExplore();

    /**
     * 获取发现参数
     */
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("found/getbanner")
    Observable<ResponseData<List<Banner>>> getBanners();

    /**
     * 获取红包记录
     */
    @FormUrlEncoded
    @POST("hongbao/gethongbaohistory")
    Observable<ResponseData<PageResult<RedEnvelope>>> getRedEnvHistories(@Field("uid") long userId,
                                                                         @Field("page") int pageNumber,
                                                                         @Field("pagesize") int pageSize);

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
     * 用户Summary
     */
    @FormUrlEncoded
    @POST("user/user/gethongbaostatistic")
    Observable<ResponseData<UserSummary>> getUserSummary(@Field("userid") long userId);

    /**
     * 获取用户基本信息
     */
    @FormUrlEncoded
    @POST("room/getuserinfo")
    Observable<ResponseData<UserSummary>> getUserInfo(@Field("uid") long userId);

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
     * 用户福卡总剩余时间
     */
    @FormUrlEncoded
    @POST("fuka/consume/getlefttime")
    Observable<ResponseData<LeftTimeInfo>> getLuckCardTotalLeftTime(@Field("userid") long userId);

    /**
     * 获取红包头条
     */
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("user/video/gettoutiao")
    Observable<ResponseData<List<RedEnvHot>>> getRedEnvHots();

    /**
     * 用户赠送购买福卡数据统计
     */
    @FormUrlEncoded
    @POST("fuka/order/getsum")
    Observable<ResponseData<LuckCardSummary>> getLuckCardSummary(@Field("userid") long userId);

    /**
     * 是否首次提现
     */
    @FormUrlEncoded
    @POST("user/withdraw/isfirsttime")
    Observable<ResponseData<Boolean>> isFirstWithdrawal(@Field("userid") long userId);

    /**
     * 获取正在提现的记录
     */
    @FormUrlEncoded
    @POST("user/withdraw/gettokening")
    Observable<ResponseData<List<Withdrawal>>> getCurrentWithdrawals(@Field("userid") long userId);


    /**
     * 入提现记录生成口令
     */
    @FormUrlEncoded
    @POST("user/withdraw/setwithdraw")
    Observable<ResponseData<Withdrawal>> saveWithdrawal(@Field("userid") long userId,
                                                        @Field("amount") float withdrawalAmount);

    /**
     * 提现记录
     */
    @FormUrlEncoded
    @POST("user/withdraw/getrecord")
    Observable<ResponseData<PageResult<Withdrawal>>> getWithdrawalHistories(@Field("userid") long userId,
                                                                            @Field("pagenumber") int pageNumber,
                                                                            @Field("pagesize") int pageSize);

    /**
     * 清空提现记录
     */
    @FormUrlEncoded
    @POST("user/withdraw/clean")
    Observable<ResponseData<String>> cleanWithdrawalHistories(@Field("userid") long userId);

    /**
     * VIP创建房间
     */
    @FormUrlEncoded
    @POST("room/addviproom")
    Observable<ResponseData<RoomIdInfo>> createVipRoom(@Field("uid") long userId,
                                                       @Field("name") String name,
                                                       @Field("desc") String desc,
                                                       @Field("icon") String icon);

    /**
     * 用户是否VIP
     */
    @FormUrlEncoded
    @POST("user/user/isvip")
    Observable<ResponseData<VipInfo>> isVip(@Field("userid") long userId);

    /**
     * 用户房间浏览记录
     */
    @FormUrlEncoded
    @POST("user/user/getscanroomhistory")
    Observable<ResponseData<List<RoomHistory>>> getRoomHistories(@Field("userid") long userId,
                                                                 @Field("pagesize") int pageSize);

    /**
     * 用户商品浏览记录
     */
    @FormUrlEncoded
    @POST("user/user/getscangoodshistory")
    Observable<ResponseData<List<GoodsHistory>>> getGoodsHistories(@Field("userid") long userId,
                                                                   @Field("pagesize") int pageSize);

    /**
     * 用户浏览记录
     */
    @FormUrlEncoded
    @POST("user/video/getwatchhistory")
    Observable<ResponseData<PageResult<VideoHistory>>> getVideoHistories(@Field("userid") long userId,
                                                                         @Field("page") int pageNumber,
                                                                         @Field("pagesize") int pageSize);

    /**
     * 获取优惠券推荐
     */
    @FormUrlEncoded
    @POST("ticket/getrecommendtickets")
    Observable<ResponseData<List<Goods>>> getRecommendGoods(@Field("count") int count);

    /**
     * 猜你喜欢
     */
    @FormUrlEncoded
    @POST("ticket/getfavoritetickets")
    Observable<ResponseData<List<Goods>>> guessYouLike(@Field("id") long id,
                                                       @Field("count") int count);

    /**
     * 搜索房间（按ID号）
     */
    @FormUrlEncoded
    @POST("room/searchroom")
    Observable<ResponseData<Room>> searchRoom(@Field("roomid") String keyword);

    /**
     * 进入房间
     */
    @FormUrlEncoded
    @POST("room/joinroom")
    Observable<ResponseData<Integer>> joinRoom(@Field("uid") long userId,
                                               @Field("roomid") long roomId);

    /**
     * 退出房间
     */
    @FormUrlEncoded
    @POST("room/exitroom")
    Observable<ResponseData<String>> exitRoom(@Field("uid") long userId,
                                              @Field("roomid") long roomId);

    /**
     * 退获取增量消息（每5S获取一次）
     */
    @FormUrlEncoded
    @POST("roommsg/getaddmsg")
    Observable<ResponseData<List<Message>>> getAddedMsg(@Field("uid") long userId,
                                                        @Field("roomid") long roomId);

    /**
     * 获取红包详情
     */
    @FormUrlEncoded
    @POST("hongbao/gethongbaodetail")
    Observable<ResponseData<SnatchingDetail>> getSnatchingDetail(@Field("uid") long userId,
                                                                 @Field("msgid") long msgId,
                                                                 @Field("page") int pageNumber,
                                                                 @Field("pagesize") int pageSize);

    /**
     * 获取房间基本信息
     */
    @FormUrlEncoded
    @POST("room/getroominfo")
    Observable<ResponseData<Room>> getRoomDetail(@Field("roomid") long roomId);

    /**
     * 获取房间基本信息
     */
    @FormUrlEncoded
    @POST("room/getviproominfo")
    Observable<ResponseData<RoomAndCreator>> getRoomDetailAndCreator(@Field("roomid") long roomId);

    /**
     * 获取房间用户列表
     */
    @FormUrlEncoded
    @POST("room/getroomuserlist")
    Observable<ResponseData<Users>> getRoomMembers(@Field("uid") long userId,
                                                   @Field("roomid") long roomId,
                                                   @Field("page") int pageNumber,
                                                   @Field("pagesize") int pageSize);

    /**
     * 删除VIP房间中的成员
     */
    @FormUrlEncoded
    @POST("room/deleteviproomuser")
    Observable<ResponseData<String>> deleteRoomMembers(@Field("uid") long userId,
                                                       @Field("roomid") long roomId,
                                                       @Field("uids") String userIds);

    /**
     * 获取房间用户列表
     */
    @FormUrlEncoded
    @POST("rank/getroomluckrank")
    Observable<ResponseData<List<RoomBillboard>>> getRoomBillboards(@Field("roomid") long roomId);

    /**
     * 打开红包
     */
    @FormUrlEncoded
    @POST("hongbao/openhongbao")
    Observable<ResponseData<OpenResult>> openRedEnvelope(@Field("uid") long userId,
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
     * 获取推荐视频
     */
    @FormUrlEncoded
    @POST("user/video/getrecommendvideos")
    Observable<ResponseData<List<Video>>> getRecommendVideos(@Field("count") int count);

    /**
     * 添加视频浏览记录
     */
    @FormUrlEncoded
    @POST("user/video/addvideowatch")
    Observable<ResponseData<String>> addVideoHistory(@Field("id") long id);

    /**
     * 看广告免费领取福卡
     */
    @FormUrlEncoded
    @POST("fuka/order/getfree")
    Observable<ResponseData<FreeLuckCard>> getFreeLuckCard(@Field("userid") long userId);

    /**
     * 费领优惠券
     */
    @FormUrlEncoded
    @POST("ticket/receiveticket")
    Observable<ResponseData<String>> receiveTicket(@Field("uid") long userId,
                                                   @Field("ticketid") long ticketId);

    @FormUrlEncoded
    @POST("user/user/cleanscanhistory")
    Observable<ResponseData<String>> cleanHistories(@Field("userid") long userId);

    @FormUrlEncoded
    @POST("user/vip/setrecord")
    Observable<ResponseData<WeChatPayOrder>> createBuyingVipOrder(@Field("userid") long userId,
                                                                  @Field("month") int month);

    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("app/getlinks")
    Observable<ResponseData<AppLinks>> getAppLinks();

    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("fuka/card/getcards")
    Observable<ResponseData<List<LuckCardCombo>>> getLuckCardCombo();

    @FormUrlEncoded
    @POST("fuka/order/setorder")
    Observable<ResponseData<WeChatPayOrder>> createBuyingLuckCardOrder(@Field("userid") long userId,
                                                                       @Field("cardid") long cardId,
                                                                       @Field("number") int count);

    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("user/vip/getvips")
    Observable<ResponseData<List<VipCombo>>> getVipCombo();

}
