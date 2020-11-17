package com.example.anchovyfeeder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmListAdaper extends RecyclerView.Adapter<AlarmListAdaper.ViewHolder> {
    private ArrayList<AlarmListItem> mData = null;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    AlarmListAdaper(ArrayList<AlarmListItem> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public AlarmListAdaper.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alarm_list_item, parent, false);
        AlarmListAdaper.ViewHolder vh = new AlarmListAdaper.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(AlarmListAdaper.ViewHolder holder, int position) {

        AlarmListItem item = mData.get(position);

        holder.checkBox.setChecked(item.getUse());
        holder.Time.setText(item.getTimeToString("HH시 mm분"));
        holder.name.setText(item.getName());
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView Time, name;
        ImageButton deleteButton, editButton;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            checkBox = (CheckBox) itemView.findViewById(R.id.alarm_checkBox);
            Time = (TextView) itemView.findViewById(R.id.alarm_editTextTime);
            name = (TextView) itemView.findViewById(R.id.alarm_textView);
            editButton = (ImageButton) itemView.findViewById(R.id.alarm_imageButton_edit);
            deleteButton = (ImageButton) itemView.findViewById(R.id.alarm_imageButton_delete);


            editButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    final int pos = getAdapterPosition(); // 현재 아이템 번호
                    if (pos != RecyclerView.NO_POSITION) {
                        //Toast.makeText(view.getContext(), "dasf:" + pos, Toast.LENGTH_SHORT).show();
                        AlarmAddOrUpdateDialog aldial = new AlarmAddOrUpdateDialog(view.getContext(), AlarmListAdaper.this, mData);
                        aldial.setItemPosition(pos);
                        aldial.show();
                    }
                }
            });


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final int pos = getAdapterPosition(); // 현재 아이템 번호
                    AlertDialog.Builder dial = new AlertDialog.Builder(view.getContext());
                    dial.setTitle("주의");
                    dial.setMessage("정말 삭제하시겠습니까? 삭제 후에는 복구가 불가능합니다.");
                    dial.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Toast.makeText(view.getContext(), "dasf:" + pos, Toast.LENGTH_SHORT).show();
                                    if (pos != RecyclerView.NO_POSITION) {
                                        mData.remove(pos);
                                        notifyDataSetChanged();
                                        //notifyDataSetChanged();
                                    }
                                }
                            }
                    );
                    dial.setNegativeButton("아니오", null);
                    dial.create().show();
                }
            });
        }
    }
}