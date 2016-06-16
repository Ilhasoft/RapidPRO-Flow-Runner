package in.ureport.flowrunner.flow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ureport.flowrunner.R;
import in.ureport.flowrunner.fragments.FlowFragment;

/**
 * Created by gualberto on 6/15/16.
 */
public class DialogFlowFragment extends DialogFragment {
    private FlowFragment flowFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_container_dialog, container);
    }

    static DialogFlowFragment newInstance(FlowFragment flowFragment) {
        DialogFlowFragment dialogFlowFragment = new DialogFlowFragment();
        dialogFlowFragment.flowFragment = flowFragment;
        return dialogFlowFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChildFragmentManager().beginTransaction().replace(R.id.container, flowFragment).commit();
    }
}
