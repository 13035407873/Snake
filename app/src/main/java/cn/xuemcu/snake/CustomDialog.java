package cn.xuemcu.snake;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by 朱红晨 on 2017/5/29.
 */

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
        //this.setCancelable(false);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        //this.setCancelable(false);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        private boolean showAnimation;
        private boolean autoAdapt;
        private String maxIteration;
        private String alpha;
        private String beta;
        private String gamma;
        private String delta;
        private String everyXIterations;
        private String minSegmentLength;
        private String maxSegmentLength;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_customize, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }

            if(maxIteration != null)
                ((EditText) layout.findViewById(R.id.etMaxIteration)).setText(maxIteration);
            if(alpha != null)
                ((EditText) layout.findViewById(R.id.etAlpha)).setText(alpha);
            if(beta != null)
                ((EditText) layout.findViewById(R.id.etBeta)).setText(beta);
            if(gamma != null)
                ((EditText) layout.findViewById(R.id.etGamma)).setText(gamma);
            if(delta != null)
                ((EditText) layout.findViewById(R.id.etDelta)).setText(delta);
            if(everyXIterations != null)
                ((EditText) layout.findViewById(R.id.etEveryXIterations)).setText(everyXIterations);
            if(minSegmentLength != null)
                ((EditText) layout.findViewById(R.id.etMinSegmentLength)).setText(minSegmentLength);
            if(maxSegmentLength != null)
                ((EditText) layout.findViewById(R.id.etMaxSegmentLength)).setText(maxSegmentLength);

            ((CheckBox) layout.findViewById(R.id.cbShowAnimation)).setChecked(showAnimation);
            ((CheckBox) layout.findViewById(R.id.cbAutoAdapt)).setChecked(autoAdapt);

            dialog.setContentView(layout);
            this.contentView = layout;
            return dialog;
        }

        public String getMaxIteration() {
            return ((EditText) (this.contentView.findViewById(R.id.etMaxIteration))).getText().toString();
        }

        public void setMaxIteration(String maxIteration) {
            this.maxIteration = maxIteration;
        }

        public String getAlpha() {
            return ((EditText) (this.contentView.findViewById(R.id.etAlpha))).getText().toString();
        }

        public void setAlpha(String alpha) {
            this.alpha = alpha;
        }

        public String getBeta() {
            return ((EditText) (this.contentView.findViewById(R.id.etBeta))).getText().toString();
        }

        public void setBeta(String beta) {
            this.beta = beta;
        }

        public String getGamma() {
            return ((EditText) (this.contentView.findViewById(R.id.etGamma))).getText().toString();
        }

        public void setGamma(String gamma) {
            this.gamma = gamma;
        }

        public String getDelta() {
            return ((EditText) (this.contentView.findViewById(R.id.etDelta))).getText().toString();
        }

        public void setDelta(String delta) {
            this.delta = delta;
        }

        public String getEveryXIterations() {
            return ((EditText) (this.contentView.findViewById(R.id.etEveryXIterations))).getText().toString();
        }

        public void setEveryXIterations(String everyXIterations) {
            this.everyXIterations = everyXIterations;
        }

        public String getMinSegmentLength() {
            return ((EditText) (this.contentView.findViewById(R.id.etMinSegmentLength))).getText().toString();
        }

        public void setMinSegmentLength(String minSegmentLength) {
            this.minSegmentLength = minSegmentLength;
        }

        public String getMaxSegmentLength() {
            return ((EditText) (this.contentView.findViewById(R.id.etMaxSegmentLength))).getText().toString();
        }

        public void setMaxSegmentLength(String maxSegmentLength) {
            this.maxSegmentLength = maxSegmentLength;
        }

        public boolean getShowAnimation() {
            return ((CheckBox) (this.contentView.findViewById(R.id.cbShowAnimation))).isChecked();
        }

        public void setShowAnimation(boolean showAnimation) {
            this.showAnimation = showAnimation;
        }

        public boolean getAutoAdapt() {
            return ((CheckBox) (this.contentView.findViewById(R.id.cbAutoAdapt))).isChecked();
        }

        public void setAutoAdapt(boolean autoAdapt) {
            this.autoAdapt = autoAdapt;
        }
    }
}
