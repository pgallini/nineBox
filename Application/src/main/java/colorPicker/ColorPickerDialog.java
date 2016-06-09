package colorPicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.ninebox.nineboxapp.R;

import colorPicker.ColorPickerPalette;
import colorPicker.ColorPickerSwatch;

public class ColorPickerDialog extends DialogFragment implements ColorPickerSwatch.OnColorSelectedListener {
	protected AlertDialog mAlertDialog;
	protected int[] mColors = null;
	protected int mColumns;
	protected ColorPickerSwatch.OnColorSelectedListener mListener;
	private ColorPickerPalette mPalette;
	private ProgressBar mProgress;
	protected int mSelectedColor;
	protected int mSize;
	protected int mTitleResId = R.string.color_picker_default_title;

	public ColorPickerDialog() {
		// Empty constructor required for DialogFragment
	}

	private void refreshPalette() {
		if ((this.mPalette != null) && (this.mColors != null))
			this.mPalette.drawPalette(this.mColors, this.mSelectedColor);
	}

    public static ColorPickerDialog newInstance(int titleResId, int[] colors, int selectedColor,
                                                int columns, int size) {
        ColorPickerDialog ret = new ColorPickerDialog();
        ret.initialize(titleResId, colors, selectedColor, columns, size);
        return ret;
    }

	public void initialize(int titleId, int[] colors, int selectedColor, int columns, int size) {
		setArguments(titleId, columns, size);
		setColors(colors, selectedColor);
	}

	public void onColorSelected(int selectedColor) {
		if (this.mListener != null)
			this.mListener.onColorSelected(selectedColor);
		if ((getTargetFragment() instanceof ColorPickerSwatch.OnColorSelectedListener))
			((ColorPickerSwatch.OnColorSelectedListener) getTargetFragment()).onColorSelected(selectedColor);
		if (selectedColor != this.mSelectedColor) {
			this.mSelectedColor = selectedColor;
			this.mPalette.drawPalette(this.mColors, this.mSelectedColor);
		}
		dismiss();
	}

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (getArguments() != null) {
			this.mTitleResId = getArguments().getInt("title_id");
			this.mColumns = getArguments().getInt("columns");
			this.mSize = getArguments().getInt("size");
		}
		if (bundle != null) {
			this.mColors = bundle.getIntArray("colors");
			this.mSelectedColor = ((Integer) bundle.getSerializable("selected_color")).intValue();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
        final Activity activity = getActivity();

		View view = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null);
		this.mProgress = ((ProgressBar) view.findViewById(android.R.id.progress));
		this.mPalette = ((ColorPickerPalette) view.findViewById(R.id.color_picker));
		this.mPalette.init(this.mSize, this.mColumns, this);
		if (this.mColors != null)
			showPaletteView();
		this.mAlertDialog = new AlertDialog.Builder(activity).setTitle(this.mTitleResId).setView(view).create();
		return this.mAlertDialog;
	}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null);
        View view = inflater.inflate(R.layout.color_picker_dialog, container, false);

        this.mProgress = ((ProgressBar) view.findViewById(android.R.id.progress));
        this.mPalette = ((ColorPickerPalette) view.findViewById(R.id.color_picker));
        this.mPalette.init(this.mSize, this.mColumns, this);
        if (this.mColors != null)
            showPaletteView();
        this.mAlertDialog = new AlertDialog.Builder(getActivity()).setTitle(this.mTitleResId).setView(view).create();
        return view;
    }

	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putIntArray("colors", this.mColors);
		bundle.putSerializable("selected_color", Integer.valueOf(this.mSelectedColor));
	}

	public void setArguments(int titleId, int columns, int size) {
		Bundle bundle = new Bundle();
		bundle.putInt("title_id", titleId);
		bundle.putInt("columns", columns);
		bundle.putInt("size", size);
		setArguments(bundle);
	}

	public void setColors(int[] colors, int selected) {
		if ((this.mColors != colors) || (this.mSelectedColor != selected)) {
			this.mColors = colors;
			this.mSelectedColor = selected;
			refreshPalette();
		}
	}

    public void setColors(int[] colors ) {
        if (this.mColors != colors)  {
            this.mColors = colors;
            refreshPalette();
        }
    }

	public void setOnColorSelectedListener(ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener) {
		this.mListener = onColorSelectedListener;
	}

	public void showPaletteView() {
		if ((this.mProgress != null) && (this.mPalette != null)) {
			this.mProgress.setVisibility(View.GONE);
			refreshPalette();
			this.mPalette.setVisibility(View.VISIBLE);
		}
	}

	public void showProgressBarView() {
		if ((this.mProgress != null) && (this.mPalette != null)) {
			this.mProgress.setVisibility(View.VISIBLE);
			this.mPalette.setVisibility(View.GONE);
		}
	}
}