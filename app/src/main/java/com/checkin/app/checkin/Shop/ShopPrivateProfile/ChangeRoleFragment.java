package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.MultiSpinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChangeRoleFragment extends Fragment implements MultiSpinner.MultiSpinnerListener {
    @BindView(R.id.user_image)
    ImageView view;
    @BindView(R.id.user_fullname)
    TextView fullName;
    @BindView(R.id.spinner_roles)
    MultiSpinner vRoles;
    @BindView(R.id.save_button)
    TextView saveButton;


    @Nullable @BindView(R.id.remove_button)
    TextView removeButton;

    private MemberModel memberModel;
    private int positionF;
    Unbinder unbinder;
    private onClickButtons onInteractionListener;
    public void ChangeRoleFragment(){

    }

    @Override
    public void onItemsSelected(boolean[] selected) {

    }

    public interface onClickButtons
    {
        public void setRole(MemberModel memberModel, int position, CharSequence[] roles);

    }

    public void setOnInteractionListener(onClickButtons onInteractionListener) {
        this.onInteractionListener = onInteractionListener;
    }

    public void setMemberModel(MemberModel memberModel, int position) {
        this.memberModel = memberModel;
        this.positionF=position;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
//        if(memberModel.getRole()==null)
//        {rootView = inflater.inflate(R.layout.fragment_shop_member_add, container, false);
//            }
//        else
        {            rootView = inflater.inflate(R.layout.fragment_shop_member_edit, container, false);

        }
        unbinder = ButterKnife.bind(this, rootView);

        vRoles.setListener(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CharSequence[] roles=vRoles.getSelectedItems();
               onInteractionListener.setRole(memberModel,positionF,roles);

            }
        });
        if(removeButton!=null)
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInteractionListener.setRole(memberModel,positionF,null);
            }
        });

        return rootView;


    }



}
