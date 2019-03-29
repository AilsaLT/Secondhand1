package com.ghl.wuhan.secondhand.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.me_activity.user_login;


public class Fragment4 extends Fragment {
private ImageView iv_deng;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //  return super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_fragment4,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        iv_deng=(ImageView)getActivity().findViewById(R.id.iv_deng);
        iv_deng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),user_login.class);
                startActivity(intent);
            }
        });


    }
}
