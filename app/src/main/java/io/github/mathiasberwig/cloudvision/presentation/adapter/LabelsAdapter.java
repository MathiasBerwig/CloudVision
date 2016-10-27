package io.github.mathiasberwig.cloudvision.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.data.model.LabelInfo;

/**
 * Adapter to use {@link RecyclerView} with {@link LabelInfo}. <p/>
 *
 * Created by MathiasBerwig on 12/04/16.
 */
public class LabelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> contents;

    private static final int TYPE_HINT = 0;
    private static final int TYPE_CELL = 1;

    /**
     * Default constructor.
     *
     * @param hint Hint that will be shown as a card on the first item of list.
     * @param labelsInfo The annotations of the image.
     */
    public LabelsAdapter(String hint, List<LabelInfo> labelsInfo) {
        this.contents = new ArrayList<>(labelsInfo.size() + 1);
        this.contents.add(hint);
        this.contents.addAll(labelsInfo);
    }

    @Override
    public int getItemViewType(int position) {
        // We should use instance of operator to determine the type of view, but the hint will
        // always be the first, so we can code it this way
        switch (position) {
            case 0: return TYPE_HINT;
            default: return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case TYPE_HINT: {
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
            case TYPE_HINT: {
                // Set the hint of Label
                final TextView txtHint = ((HintViewHolder) holder).txtLabelHint;
                final String hint = (String) contents.get(position);
                txtHint.setText(hint);
                break;
            }
            case TYPE_CELL: {
                // Set the score of recognized tag in the ProgressBar
                final Float score = ((LabelInfo) contents.get(position)).getScore();
                ((LabelViewHolder) holder).progressBar.setProgress(score);

                // Set the description and score of recognized tag in the TextViews
                String description = ((LabelInfo) contents.get(position)).getDescription();
                ((LabelViewHolder) holder).txtLabel.setText(description);
                // Format the description to "0%"
                String textScore = String.format(Locale.getDefault(), "%1$.0f%%", score * 100);
                ((LabelViewHolder) holder).txtLabelScore.setText(textScore);
                break;
            }
        }
    }

    /**
     * ViewHolder to store a {@link TextView} that will show a hint to the user.
     */
    private static class HintViewHolder extends RecyclerView.ViewHolder {
        TextView txtLabelHint;

        HintViewHolder(View v) {
            super(v);
            txtLabelHint = (TextView) v.findViewById(R.id.txt_label_hint);
        }
    }

    /**
     * ViewHolder to store a {@link IconRoundCornerProgressBar ProgressBar} and a {@link TextView}
     * with annotations about the image.
     */
    private static class LabelViewHolder extends RecyclerView.ViewHolder {
        RoundCornerProgressBar progressBar;
        TextView txtLabel;
        TextView txtLabelScore;

        LabelViewHolder(View v) {
            super(v);
            progressBar = (RoundCornerProgressBar) v.findViewById(R.id.pb_label_score);
            txtLabel = (TextView) v.findViewById(R.id.txt_label);
            txtLabelScore = (TextView) v.findViewById(R.id.txt_label_score);
        }
    }
}