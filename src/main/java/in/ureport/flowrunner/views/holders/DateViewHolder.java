package in.ureport.flowrunner.views.holders;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class DateViewHolder extends OpenFieldViewHolder implements DatePickerDialog.OnDateSetListener {

    public DateViewHolder(View itemView, FlowDefinition flowDefinition) {
        super(itemView, flowDefinition);
    }

    @Override
    public void bindView(FlowRule rule, String language, RulesetResponse currentResponse) {
        super.bindView(rule, language, currentResponse);
        openField.setFocusable(false);
        openField.setOnClickListener(onDateSetClickListener);

        if(isCurrentRule(currentResponse)) {
            openField.setText(currentResponse.getResponse());
        } else {
            openField.setText(null);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(onResponseChangedListener != null) {
            String response = FlowRunnerManager.getDefaultDateFormat().format(getDate(year, monthOfYear, dayOfMonth));
            openField.setText(response);
            onResponseChangedListener.onResponseChanged(rule, response);
        }
    }

    @NonNull
    private Date getDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        return calendar.getTime();
    }

    private View.OnClickListener onDateSetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(itemView.getContext(), DateViewHolder.this
                    , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    };
}
