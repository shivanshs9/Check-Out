package com.checkin.app.checkin.Manager;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Manager.Fragment.ManagerSessionEventFragment;
import com.checkin.app.checkin.Manager.Fragment.ManagerSessionOrderFragment;
import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Data.Message.Constants.KEY_DATA;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_BILL_CHANGE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_MEMBER_CHANGE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_UPDATE_ORDER;

public class ManagerSessionActivity extends AppCompatActivity implements ManagerSessionOrderFragment.ManagerOrdersInteraction {
    private static final String TAG = ManagerSessionActivity.class.getSimpleName();

    public static final String KEY_SESSION_PK = "manager.session_pk";
    public static final String KEY_SHOP_PK = "manager.shop_pk";

    @BindView(R.id.tv_ms_order_new_count)
    TextView tvCountOrdersNew;
    @BindView(R.id.tv_ms_order_progress_count)
    TextView tvCountOrdersInProgress;
    @BindView(R.id.tv_ms_order_done_count)
    TextView tvCountOrdersDelivered;
    @BindView(R.id.tv_manager_session_bill)
    TextView tvCartItemPrice;
    @BindView(R.id.tv_manager_session_waiter)
    TextView tvWaiterName;
    @BindView(R.id.tv_manager_session_table)
    TextView tvTable;
    @BindView(R.id.im_manager_session_waiter)
    ImageView imWaiterPic;
    @BindView(R.id.container_ms_order_new)
    LinearLayout containerMsOrderNew;
    @BindView(R.id.container_ms_order_progress)
    LinearLayout containerMsOrderProgress;
    @BindView(R.id.container_ms_order_done)
    LinearLayout containerMsOrderDone;

    private ManagerSessionOrderFragment mOrderFragment;
    private ManagerSessionEventFragment mEventFragment;
    private ManagerSessionViewModel mViewModel;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message;
            try {
                message = ((MessageModel) intent.getSerializableExtra(KEY_DATA));
                if (message == null)
                    return;
            } catch (ClassCastException e) {
                Log.e(TAG, "Invalid message object received.");
                e.printStackTrace();
                return;
            }

            long sessionPk = message.getTarget().getPk();
            if (mViewModel.getSessionPk() != sessionPk)
                return;

            BriefModel user;
            SessionOrderedItemModel orderedItemModel;
            switch (message.getType()) {
                case MANAGER_SESSION_NEW_ORDER:
                    orderedItemModel = message.getRawData().getSessionOrderedItem();
                    ManagerSessionActivity.this.addNewOrder(orderedItemModel);
                    break;
                case MANAGER_SESSION_EVENT_SERVICE:
                case MANAGER_SESSION_EVENT_CONCERN:
                case MANAGER_SESSION_CHECKOUT_REQUEST:
                    ManagerSessionActivity.this.addSessionEvent(message.getRawData().getSessionEventBrief());
                    break;
                case MANAGER_SESSION_HOST_ASSIGNED:
                    user = message.getObject().getBriefModel();
                    ManagerSessionActivity.this.updateSessionHost(user);
                    break;
                case MANAGER_SESSION_BILL_CHANGE:
                    ManagerSessionActivity.this.updateBill(message.getRawData().getSessionBillTotal());
                    break;
                case MANAGER_SESSION_MEMBER_CHANGE:
                    ManagerSessionActivity.this.updateMemberCount(message.getRawData().getSessionCustomerCount());
                    break;
                case MANAGER_SESSION_UPDATE_ORDER:
                    long orderPk = message.getRawData().getSessionOrderId();
                    ManagerSessionActivity.this.updateOrderStatus(orderPk, message.getRawData().getSessionEventStatus());
                    break;
            }
        }
    };

    private void updateSessionHost(BriefModel user) {
        SessionBriefModel data = mViewModel.getSessionData();
        if (data != null) {
            data.setHost(user);
        }
        mViewModel.updateSessionData(data);
    }

    private void addSessionEvent(ManagerSessionEventModel eventModel) {
        mViewModel.addEventData(eventModel);
    }

    private void updateBill(double bill) {
        SessionBriefModel data = mViewModel.getSessionData();
        if (data != null) {
            data.setBill(bill);
        }
        mViewModel.updateSessionData(data);
    }

    private void updateMemberCount(int count) {
        SessionBriefModel data = mViewModel.getSessionData();
        if (data != null) {
            data.setCustomerCount(count);
        }
        mViewModel.updateSessionData(data);
    }

    private void updateOrderStatus(long orderPk, SessionChatModel.CHAT_STATUS_TYPE statusType) {
        OrderStatusModel data = new OrderStatusModel(orderPk, statusType);
        mViewModel.updateUiOrderStatus(data);
    }

    private void addNewOrder(SessionOrderedItemModel orderedItemModel) {
        mViewModel.addOrderData(orderedItemModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_session);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(ManagerSessionViewModel.class);
        setupUi();
        mOrderFragment = ManagerSessionOrderFragment.newInstance(this);
        mEventFragment = ManagerSessionEventFragment.newInstance();
        setupEventListing();
    }

    private void setupUi() {
        long sessionId = getIntent().getLongExtra(KEY_SESSION_PK, 0);
        long shopId = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchSessionBriefData(sessionId);
        mViewModel.setShopPk(shopId);
        mViewModel.fetchSessionOrders();

        mViewModel.getSessionBriefData().observe(this, resource -> {
            if (resource == null) return;
            SessionBriefModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    setupData(data);
                }
                case LOADING: {
                    break;
                }
                default: {
                    Log.e(resource.status.name(), resource.message == null ? "Null" : resource.message);
                }
            }
        });

        mViewModel.getCountNewOrders().observe(this, integer -> {
            if (integer == null)
                integer = 0;
            tvCountOrdersNew.setTextColor(
                    integer > 0 ? getResources().getColor(R.color.primary_red) : getResources().getColor(R.color.brownish_grey));
            tvCountOrdersNew.setText(String.valueOf(integer));
        });
        mViewModel.getCountProgressOrders().observe(this, integer -> {
            if (integer == null)
                integer = 0;
            tvCountOrdersInProgress.setText(String.valueOf(integer));
        });
        mViewModel.getCountDeliveredOrders().observe(this, integer -> {
            if (integer == null)
                integer = 0;
            tvCountOrdersDelivered.setText(String.valueOf(integer));
        });
    }

    private void setupData(SessionBriefModel data) {
        tvCartItemPrice.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(this), data.formatBill()));
        if (data.getHost() != null) {
            tvWaiterName.setText(data.getHost().getDisplayName());
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);
        } else {
            imWaiterPic.setImageResource(R.drawable.ic_waiter);
            tvWaiterName.setText(R.string.waiter_unassigned);
        }
        tvTable.setText(data.getTable());
    }

    @OnClick(R.id.im_ms_bottom_swipe_up)
    public void onSwipeUp() {
        setupOrdersListing();
    }

    private void setupOrdersListing() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_manager_session_fragment, mOrderFragment)
                .commit();
    }

    private void setupEventListing() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_manager_session_events, mEventFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mOrderFragment != null && !mOrderFragment.onBackPressed())
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageModel.MESSAGE_TYPE[] types = new MessageModel.MESSAGE_TYPE[]{
                MANAGER_SESSION_NEW_ORDER, MANAGER_SESSION_EVENT_SERVICE, MANAGER_SESSION_CHECKOUT_REQUEST,
                MANAGER_SESSION_EVENT_CONCERN, MANAGER_SESSION_HOST_ASSIGNED, MANAGER_SESSION_BILL_CHANGE,
                MANAGER_SESSION_MEMBER_CHANGE, MANAGER_SESSION_UPDATE_ORDER
        };
        MessageUtils.registerLocalReceiver(this, mReceiver, types);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }

    @OnClick(R.id.im_manager_session_back)
    public void goBack() {
        onBackPressed();
    }

    @OnClick({R.id.container_ms_order_new, R.id.container_ms_order_progress, R.id.container_ms_order_done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.container_ms_order_new:
            case R.id.container_ms_order_progress:
            case R.id.container_ms_order_done:
                setupOrdersListing();
                break;
        }
    }
}