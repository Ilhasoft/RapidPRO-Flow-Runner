package in.ureport.flowrunner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import in.ureport.flowrunner.R;

/**
 * Created by johncordeiro on 09/11/15.
 */
public class InfoFragment extends Fragment {
    private onDialogClickExit onDialogClickExit;
    private int responseButtonRes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        return view;
    }

    public static InfoFragment newInstance(onDialogClickExit onDialogClickExit, int responseButtonRes) {
        InfoFragment fragment = new InfoFragment();
        fragment.setOnDialogClickExit(onDialogClickExit);
        fragment.setResponseButtonRes(responseButtonRes);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (onDialogClickExit != null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            Button button = (Button) layoutInflater.inflate(responseButtonRes, null);
            button.setText(R.string.label_ok);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDialogClickExit.onClickExit();
                }
            });
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
            linearLayout.addView(button);
        }
    }

    public void setResponseButtonRes(int responseButtonRes) {
        this.responseButtonRes = responseButtonRes;
    }

    public void setOnDialogClickExit(onDialogClickExit onDialogClickExit) {
        this.onDialogClickExit = onDialogClickExit;
    }

    public interface onDialogClickExit {
        void onClickExit();
    }

}
