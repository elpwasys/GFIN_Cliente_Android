package br.com.wasys.gfin.cheqfast.cliente.widget;

import android.content.Context;
import android.util.AttributeSet;

import br.com.wasys.library.text.MoneyTextWatcher;
import br.com.wasys.library.utils.FieldUtils;

/**
 * Created by pascke on 25/06/17.
 */

public class AppMoneyEditText extends AppNumberEditText {

    public AppMoneyEditText(Context context) {
        super(context);
        configure();
    }

    public AppMoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure();
    }

    public AppMoneyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configure();
    }

    private void configure() {
        setLines(1);
        setSingleLine(true);
        new MoneyTextWatcher(this);
    }

    @Override
    public boolean isValid() {
        if (isEmpty()) {
            return true;
        }
        Double value = FieldUtils.getValue(Double.class, this);
        return value != null;
    }
}