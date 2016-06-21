package io.github.mathiasberwig.cloudvision.presentation.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.data.model.EntityProperty;
import io.github.mathiasberwig.cloudvision.data.model.LogoInfo;

/**
 * Adapter to use {@link RecyclerView} with {@link LogoInfo}.
 *
 * Created by mathias.berwig on 16/06/2016.
 */
public class LogoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LogoInfo content;

    static final int TYPE_DESCRIPTION = 0;
    static final int TYPE_CELL = 1;

    public LogoAdapter(LogoInfo logoInfo) {
        this.content = logoInfo;
    }

    @Override
    public int getItemViewType(int position) {
        // We should use instance of operator to determine the type of view, but the hint will
        // always be the first, so we can code it this way
        switch (position) {
            case 0: return TYPE_DESCRIPTION;
            default: return TYPE_CELL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case TYPE_DESCRIPTION: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_card_wiki_article_info, parent, false);
                return new DescriptionViewHolder(view) {};
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_brand_property, parent, false);
                return new BrandPropertyViewHolder(view) {};
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_DESCRIPTION: {
                // Set the name, description and link of article
                ((DescriptionViewHolder) holder).txtArticleName.setText(content.getBrandName());
                ((DescriptionViewHolder) holder).txtArticleDescription.setText(content.getDescription());
                ((DescriptionViewHolder) holder).btnArticleInfoMore.setOnClickListener(this.btnBrandInfoMoreOnClick);
                break;
            }
            case TYPE_CELL: {
                EntityProperty prop = content.getProperties().get(position - 1);
                // Set the icon, property name and value then, if exists, a OnClickListener
                ((BrandPropertyViewHolder) holder).imgPropertyIcon.setImageResource(prop.getIcon());
                ((BrandPropertyViewHolder) holder).txtPropertyTitle.setText(prop.getName());
                ((BrandPropertyViewHolder) holder).txtPropertyValue.setText(prop.getValue());
                if (prop.getOnClickListener() != null) {
                    ((BrandPropertyViewHolder) holder).cardView.setOnClickListener(prop.getOnClickListener());
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        // Each property is a cell + the description
        return content.getProperties().size() + 1;
    }

    /**
     * ViewHolder to store {@link TextView TextViews} that will show the article name and description
     * of an Article on Wikipedia, plus a {@link AppCompatButton Button} to open it on the default
     * browser.
     */
    public static class DescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView txtArticleName;
        TextView txtArticleDescription;
        AppCompatButton btnArticleInfoMore;

        public DescriptionViewHolder(View v) {
            super(v);
            txtArticleName = (TextView) v.findViewById(R.id.txt_article_name);
            txtArticleDescription = (TextView) v.findViewById(R.id.txt_article_description);
            btnArticleInfoMore = (AppCompatButton) v.findViewById(R.id.btn_open_in_wikipedia);
        }
    }

    /**
     * ViewHolder to store the icon and {@link TextView TextViews} that will show extra properties of
     * a brand.
     */
    public static class BrandPropertyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgPropertyIcon;
        TextView txtPropertyTitle;
        TextView txtPropertyValue;

        public BrandPropertyViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imgPropertyIcon = (ImageView) v.findViewById(R.id.img_property_icon);
            txtPropertyTitle = (TextView) v.findViewById(R.id.txt_property_title);
            txtPropertyValue = (TextView) v.findViewById(R.id.txt_property_value);
        }
    }

    /**
     * OnClickListener for button "More" on Wikipedia's Article Info. It will open the Wikipedia
     * Article in the default browser.
     */
    private View.OnClickListener btnBrandInfoMoreOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(content.getWikipediaArticleUrl()));
            v.getContext().startActivity(intent);
        }
    };
}
