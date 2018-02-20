package net.north101.android.ghplayertracker;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.north101.android.ghplayertracker.data.SelectableCharacter;

public class CharacterViewHolder extends BaseViewHolder<SelectableCharacter> {
    public static int layout = R.layout.character_item_view;

    private final ImageView iconView;
    private final TextView nameView;
    private final TextView levelView;

    public static CharacterViewHolder inflate(ViewGroup parent) {
        return new CharacterViewHolder(inflate(parent, layout));
    }

    public CharacterViewHolder(View view) {
        super(view);

        this.iconView = view.findViewById(R.id.class_icon);
        this.nameView = view.findViewById(R.id.name);
        this.levelView = view.findViewById(R.id.level);
    }

    public void bind(SelectableCharacter item) {
        super.bind(item);

        this.iconView.setImageResource(Util.getImageResource(itemView.getContext(), item.character.getCharacterClass().id));
        this.nameView.setText(item.character.getName());
        this.levelView.setText(String.valueOf(item.character.getCurrentLevel().level));
        if (item.selected) {
            itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }
}
