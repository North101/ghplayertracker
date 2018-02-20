package net.north101.android.ghplayertracker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import net.north101.android.ghplayertracker.data.CharacterPerk;

public class PerkViewHolder extends BaseViewHolder<CharacterPerk> {
    public static int layout = R.layout.perk_item_view;

    CheckBox perk1;
    CheckBox perk2;
    CheckBox perk3;
    TextView textView;

    public static PerkViewHolder inflate(ViewGroup parent) {
        return new PerkViewHolder(inflate(parent, layout));
    }

    public PerkViewHolder(View itemView) {
        super(itemView);

        perk1 = itemView.findViewById(R.id.perk1);
        perk2 = itemView.findViewById(R.id.perk2);
        perk3 = itemView.findViewById(R.id.perk3);
        textView = itemView.findViewById(R.id.textView);
    }

    void setText(String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        Context context = itemView.getContext();
        Resources res = context.getResources();
        int index = 0;
        int offset = 0;
        while ((index + offset) < text.length()) {
            int start = text.indexOf("[[", index + offset);
            if (start == -1) break;

            int end = text.indexOf("]]", start) + 2;
            if (end == -1) break;

            String icon = text.substring(start + 2, end - 2);

            int iconId = res.getIdentifier(icon, "drawable", context.getPackageName());
            if (iconId == 0) {
                offset++;
            } else {
                ssb.append(new SpannableString(text.substring(index, start)));
                Bitmap image = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, iconId), 50, 50, false);
                ssb.append(text.substring(start, end), new ImageSpan(context, image, ImageSpan.ALIGN_BOTTOM), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                index = end;
                offset = 0;
            }
        }
        if (index < text.length()) {
            ssb.append(new SpannableString(text.substring(index)));
        }

        textView.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    @Override
    public void bind(CharacterPerk item) {
        super.bind(item);

        perk2.setVisibility(item.perk.count >= 2 ? View.VISIBLE : View.GONE);
        perk3.setVisibility(item.perk.count >= 3 ? View.VISIBLE : View.GONE);

        setText(item.perk.text);
        updatePerks();
    }

    void updatePerks() {
        perk1.setChecked(item.ticks >= 1);
        perk2.setChecked(item.ticks >= 2);
        perk3.setChecked(item.ticks >= 3);
    }

    @Override
    public void onClick(View view) {
        if (this.item.ticks == this.item.perk.count) {
            this.item.ticks = 0;
        } else {
            this.item.ticks += 1;
        }
        updatePerks();

        super.onClick(view);
    }
}
