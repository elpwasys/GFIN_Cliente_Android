package br.com.wasys.gfin.cheqfast.cliente.widget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.library.utils.AndroidUtils;
import br.com.wasys.library.utils.DateUtils;
import br.com.wasys.library.utils.FieldUtils;

/**
 * Created by pascke on 25/06/17.
 */

public class AppDateEditText extends AppEditText implements DatePickerDialog.OnDateSetListener {

    private Dialog mDialog;

    public AppDateEditText(Context context) {
        super(context);
        configure();
    }

    public AppDateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure();
    }

    public AppDateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configure();
    }

    private void configure() {
        Context context = getContext();
        setLines(1);
        setSingleLine(true);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_today);
        drawable.mutate().setAlpha(128);
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mDialog = new DatePickerDialog(context, this, mYear, mMonth, mDay) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        };
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DATE, dayOfMonth);
        calendar.set(Calendar.MONTH, monthOfYear);
        Date date = calendar.getTime();
        String text = DateUtils.format(date, DateUtils.DateType.DATE_BR.getPattern());
        setText(text);
        setSelection(text.length());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = super.onTouchEvent(event);
        AndroidUtils.hideKeyboard(getContext(), this);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        return handled;
    }

    @Override
    public boolean isValid() {
        if (isEmpty()) {
            return true;
        }
        Date value = getValue();
        return value != null;
    }

    public Date getValue() {
        return FieldUtils.getValue(Date.class, this);
    }

    public void setValue(Date value) {
        FieldUtils.setText(this, value);
    }
}
