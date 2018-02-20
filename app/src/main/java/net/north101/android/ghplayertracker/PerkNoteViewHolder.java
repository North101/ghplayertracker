package net.north101.android.ghplayertracker;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import net.north101.android.ghplayertracker.data.PerkNote;

public class PerkNoteViewHolder extends BaseViewHolder<PerkNote> {
    public static int layout = R.layout.perk_note_item_view;

    CheckBox perk1View;
    CheckBox perk2View;
    CheckBox perk3View;

    public static PerkNoteViewHolder inflate(ViewGroup parent) {
        return new PerkNoteViewHolder(inflate(parent, layout));
    }

    public PerkNoteViewHolder(View itemView) {
        super(itemView);

        perk1View = itemView.findViewById(R.id.perk1);
        perk2View = itemView.findViewById(R.id.perk2);
        perk3View = itemView.findViewById(R.id.perk3);
    }

    @Override
    public void bind(PerkNote item) {
        super.bind(item);

        updatePerks();
    }

    void updatePerks() {
        int ticks = item.getTicks();
        perk1View.setChecked(ticks >= 1);
        perk2View.setChecked(ticks >= 2);
        perk3View.setChecked(ticks >= 3);
    }

    @Override
    public void onClick(View view) {
        int ticks = item.getTicks();
        if (ticks == PerkNote.MAX_TICKS) {
            this.item.setTicks(0);
        } else {
            this.item.setTicks(ticks + 1);
        }
        updatePerks();

        super.onClick(view);
    }
}
