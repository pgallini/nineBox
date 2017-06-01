package common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * Created by Paul Gallini on 1/29/17.
 *
 * By extending EditText, I can combine a multiline input type with a DONE button
 *   (versus the CR button).  THis is used in candidates_entry.xml.  See the second answer on this post:
 * http://stackoverflow.com/questions/2986387/multi-line-edittext-with-done-action-button
 *
 */
public class ActionEditText extends android.support.v7.widget.AppCompatEditText

{
    public ActionEditText(Context context)
    {
        super(context);
    }

    public ActionEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ActionEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }
}