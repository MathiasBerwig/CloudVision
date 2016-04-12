package io.github.mathiasberwig.cloudvision.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.mathiasberwig.cloudvision.R;

/**
 * Adapter to use {@link RecyclerView} with {@link EntityAnnotation}. <p/>
 *
 * Created by MathiasBerwig on 12/04/16.
 */
public class LabelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> contents;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    /**
     * Default constructor.
     *
     * @param hint Hint that will be shown as a card on the first item of list.
     * @param annotations The annotations of the image.
     */
    public LabelsAdapter(String hint, List<EntityAnnotation> annotations) {
        this.contents = new ArrayList<>(annotations.size() + 1);
        this.contents.add(hint);
        this.contents.addAll(annotations);
    }

    @Override
    public int getItemViewType(int position) {
        // We should use instance of operator to determine the type of view, but the hint will
        // always be the first, so we can code it this way
        switch (position) {
            case 0: return TYPE_HEADER;
            default: return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        // We don't count the header (hint) here
        return contents.size() - 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_hint, parent, false);
                return new HintViewHolder(view) {};
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_label, parent, false);
                return new LabelViewHolder(view) {};
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER: {
                // Set the hint of Label
                final TextView txtHint = ((HintViewHolder) holder).txtHint;
                final String hint = (String) contents.get(position);
                txtHint.setText(hint);

                break;
            }
            case TYPE_CELL: {
                // Set the score of recognized tag in the ProgressBar
                final IconRoundCornerProgressBar mProgressBar = ((LabelViewHolder) holder).progressBar;
                Float score = ((EntityAnnotation) contents.get(position)).getScore();
                mProgressBar.setProgress(score == null ? 0 : score);

                // Set the description of recognized tag in the TextView
                final TextView mTextView = ((LabelViewHolder) holder).txtProgress;
                String description = ((EntityAnnotation) contents.get(position)).getDescription();
                // Format the description to "0% - tag"
                description = String.format(Locale.getDefault(), "%1$.0f%%  - %2$s", score == null ? 0f : score * 100, description);
                mTextView.setText(description);
                break;
            }
        }
    }

    /**
     * ViewHolder to store a {@link TextView} that will show a hint to the user.
     */
    public static class HintViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHint;

        public HintViewHolder(View v) {
            super(v);
            txtHint = (TextView) v.findViewById(R.id.txt_hint);
        }
    }

    /**
     * ViewHolder to store a {@link IconRoundCornerProgressBar ProgressBar} and a {@link TextView}
     * with annotations about the image.
     */
    public static class LabelViewHolder extends RecyclerView.ViewHolder {
        public IconRoundCornerProgressBar progressBar;
        public TextView txtProgress;

        public LabelViewHolder(View v) {
            super(v);
            progressBar = (IconRoundCornerProgressBar) v.findViewById(R.id.pb_image_label);
            txtProgress = (TextView) v.findViewById(R.id.txt_progress);
        }
    }
}