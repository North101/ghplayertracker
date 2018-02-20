package net.north101.android.ghplayertracker;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ClassViewHolder extends BaseViewHolder<String> {
    public static int layout = R.layout.class_item_view;

    private final ImageView itemView;

    public static ClassViewHolder inflate(ViewGroup parent) {
        return new ClassViewHolder(inflate(parent, layout));
    }

    public ClassViewHolder(View view) {
        super(view);

        this.itemView = view.findViewById(R.id.class_icon);
    }

    public void bind(String item) {
        super.bind(item);

        this.itemView.setImageResource(Util.getImageResource(itemView.getContext(), item));
    }
}
