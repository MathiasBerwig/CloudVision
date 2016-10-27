package io.github.mathiasberwig.cloudvision.presentation.adapter;

import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.data.model.EntityProperty;
import io.github.mathiasberwig.cloudvision.data.model.LogoInfo;
import io.github.mathiasberwig.cloudvision.presentation.decoder.SvgDecoder;
import io.github.mathiasberwig.cloudvision.presentation.decoder.SvgDrawableTranscoder;
import io.github.mathiasberwig.cloudvision.presentation.decoder.SvgSoftwareLayerSetter;

/**
 * Adapter to use {@link RecyclerView} with {@link LogoInfo}.
 *
 * Created by mathias.berwig on 16/06/2016.
 */
public class LogoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LogoInfo content;
    private Fragment fragment;

    /**
     * {@code true} if the {@link #content LogoInfo} has a logo URL.
     */
    private boolean hasLogo;

    private static final int TYPE_DESCRIPTION = 0;
    private static final int TYPE_CELL = 1;
    private static final int TYPE_LOGO = 2;

    public LogoAdapter(Fragment fragment, LogoInfo logoInfo) {
        this.fragment = fragment;
        this.content = logoInfo;
        this.hasLogo = content.getLogoUrl() != null && !content.getLogoUrl().trim().isEmpty();
    }

    @Override
    public int getItemViewType(int position) {
        // The first item is always the description; and the last is the logo. The items in the middle
        // are properties (cells).
        switch (position) {
            case 0: return TYPE_DESCRIPTION;
            case 1: return hasLogo ? TYPE_LOGO : TYPE_CELL;
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
            case TYPE_LOGO: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_logo_image, parent, false);
                return new LogoPropertyViewHolder(view) {};
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
                EntityProperty prop = content.getProperties().get(position - (hasLogo ? 2 : 1));
                // Set the icon, property name and value then, if exists, a OnClickListener
                ((BrandPropertyViewHolder) holder).imgPropertyIcon.setImageResource(prop.getIcon());
                ((BrandPropertyViewHolder) holder).txtPropertyTitle.setText(prop.getName());
                ((BrandPropertyViewHolder) holder).txtPropertyValue.setText(prop.getValue());
                if (prop.getOnClickListener() != null) {
                    ((BrandPropertyViewHolder) holder).cardView.setOnClickListener(prop.getOnClickListener());
                }
                break;
            }
            case TYPE_LOGO: {
                final Uri uri = Uri.parse(content.getLogoUrl());
                prepareImageViewWithGlide((LogoPropertyViewHolder) holder, uri);
            }
        }
    }

    @Override
    public int getItemCount() {
        // Each property is a cell + the description + logo (1 if it exists, else 0)
        return content.getProperties().size() + 1 + (hasLogo ? 1 : 0);
    }

    private void prepareImageViewWithGlide(LogoPropertyViewHolder holder, Uri uri) {
        boolean svg = content.getLogoUrl().endsWith(".svg");

        GenericRequestBuilder requestBuilder = null;

        // TODO: A placeholder would be cool here, right?
        // https://futurestud.io/blog/glide-placeholders-fade-animations
        if (svg) {
            requestBuilder = Glide.with(fragment)
                    .using(Glide.buildStreamModelLoader(Uri.class, fragment.getContext()), InputStream.class)
                    .from(Uri.class)
                    .as(SVG.class)
                    .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                    .sourceEncoder(new StreamEncoder())
                    .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE) // SVG cannot be serialized so it's not worth to cache it
                    .listener(new SvgSoftwareLayerSetter<Uri>())
                    .decoder(new SvgDecoder());
        } else {
            requestBuilder = Glide.with(fragment).from(Uri.class);
        }

        //noinspection unchecked
        requestBuilder.animate(android.R.anim.fade_in).load(uri).into(holder.imgPropertyLogo);
    }

    /**
     * ViewHolder to store {@link TextView TextViews} that will show the article name and description
     * of an Article on Wikipedia, plus a {@link AppCompatButton Button} to open it on the default
     * browser.
     */
    private static class DescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView txtArticleName;
        TextView txtArticleDescription;
        AppCompatButton btnArticleInfoMore;

        DescriptionViewHolder(View v) {
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
    private static class BrandPropertyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgPropertyIcon;
        TextView txtPropertyTitle;
        TextView txtPropertyValue;

        BrandPropertyViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imgPropertyIcon = (ImageView) v.findViewById(R.id.img_property_icon);
            txtPropertyTitle = (TextView) v.findViewById(R.id.txt_property_title);
            txtPropertyValue = (TextView) v.findViewById(R.id.txt_property_value);
        }
    }

    /**
     * ViewHolder to store an {@link ImageView} that will show the logo of a brand.
     */
    private static class LogoPropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPropertyLogo;

        LogoPropertyViewHolder(View v) {
            super(v);
            imgPropertyLogo = (ImageView) v.findViewById(R.id.img_property_logo);
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
