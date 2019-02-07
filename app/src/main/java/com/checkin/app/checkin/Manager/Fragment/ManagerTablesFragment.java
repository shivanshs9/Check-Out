package com.checkin.app.checkin.Manager.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Adapter.ManagerWorkTableAdapter;
import com.checkin.app.checkin.Manager.ManagerSessionActivity;
import com.checkin.app.checkin.Manager.Model.ManagerWorkViewModel;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.EventBriefModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.checkin.app.checkin.Data.Message.Constants.KEY_DATA;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER;

public class ManagerTablesFragment extends Fragment implements ManagerWorkTableAdapter.ManagerTableInteraction {
    private static final String TAG = ManagerTablesFragment.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.rv_shop_manager_table)
    RecyclerView rvShopManagerTable;

    private ManagerWorkTableAdapter mAdapter;
    private ManagerWorkViewModel mViewModel;

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

            EventBriefModel eventModel;
            BriefModel user;
            switch (message.getType()) {
                case MANAGER_SESSION_NEW:
                    String tableName = message.getRawData().getSessionTableName();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    RestaurantTableModel tableModel = new RestaurantTableModel(message.getObject().getPk(), tableName, null, eventModel);
                    if (message.getActor().getType() == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        user = message.getActor().getBriefModel();
                        tableModel.setHost(user);
                    }
                    ManagerTablesFragment.this.addTable(tableModel);
                    break;
                case MANAGER_SESSION_NEW_ORDER:
                case MANAGER_SESSION_EVENT_SERVICE:
                case MANAGER_SESSION_EVENT_CONCERN:
                case MANAGER_SESSION_CHECKOUT_REQUEST:
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    ManagerTablesFragment.this.updateSessionEventCount(message.getTarget().getPk(), eventModel);
                    break;
                case MANAGER_SESSION_HOST_ASSIGNED:
                    user = message.getObject().getBriefModel();
                    ManagerTablesFragment.this.updateSessionHost(message.getTarget().getPk(), user);
                    break;
            }
        }
    };

    public ManagerTablesFragment() {
    }

    public static ManagerTablesFragment newInstance() {
        return new ManagerTablesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_manager_table, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ManagerWorkTableAdapter(this);
        rvShopManagerTable.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvShopManagerTable.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerWorkViewModel.class);
        mViewModel.getActiveTables().observe(this, input -> {
            if (input != null && input.data == null)
                return;
            if (input != null && input.data.size() > 0 && input.status == Resource.Status.SUCCESS) {
                mAdapter.setRestaurantTableList(input.data);
            }
        });
        mViewModel.getDetailData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                Utils.toast(requireContext(), resource.message);
                int pos = mViewModel.getTablePositionWithPk(Long.valueOf(resource.data.getPk()));
                if (pos > -1)
                    mAdapter.removeSession(pos);
            } else if (resource.status != Resource.Status.LOADING) {
                Utils.toast(requireContext(), "Error: " + resource.message);
            }
        });
    }

    private void addTable(RestaurantTableModel tableModel) {
        mViewModel.addRestaurantTable(tableModel);
    }

    private void updateSessionEventCount(long sessionPk, EventBriefModel event) {
        int pos = mViewModel.getTablePositionWithPk(sessionPk);
        RestaurantTableModel table = mViewModel.getTableWithPosition(pos);
        if (table != null) {
            table.setEvent(event);
            table.addEventCount();
            mAdapter.updateSession(pos);
        }
    }

    private void updateSessionHost(long sessionPk, BriefModel user) {
        int pos = mViewModel.getTablePositionWithPk(sessionPk);
        RestaurantTableModel table = mViewModel.getTableWithPosition(pos);
        if (table != null) {
            table.setHost(user);
            mAdapter.updateSession(pos);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MessageModel.MESSAGE_TYPE[] types = new MessageModel.MESSAGE_TYPE[] {
                MANAGER_SESSION_NEW, MANAGER_SESSION_NEW_ORDER, MANAGER_SESSION_EVENT_SERVICE, MANAGER_SESSION_CHECKOUT_REQUEST,
                MANAGER_SESSION_EVENT_CONCERN, MANAGER_SESSION_HOST_ASSIGNED
        };
        MessageUtils.registerLocalReceiver(requireContext(), mReceiver, types);
    }

    @Override
    public void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClickTable(RestaurantTableModel tableModel) {
        Intent intent = new Intent(getContext(), ManagerSessionActivity.class);
        intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, tableModel.getPk())
                .putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.getShopPk());
        startActivity(intent);

        int pos = mViewModel.getTablePositionWithPk(tableModel.getPk());
        tableModel.setEventCount(0);
        mAdapter.updateSession(pos);
    }

    @Override
    public void onMarkSessionDone(RestaurantTableModel tableModel) {
        mViewModel.markSessionDone(tableModel.getPk());
    }
}