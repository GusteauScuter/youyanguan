package com.gusteauscuter.youyanguan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.activity.BookDetailActivity;
import com.gusteauscuter.youyanguan.api.InternetServiceApi;
import com.gusteauscuter.youyanguan.api.InternetServiceApiImpl;
import com.gusteauscuter.youyanguan.common.PublicString;
import com.gusteauscuter.youyanguan.domain.BookBorrowed;
import com.gusteauscuter.youyanguan.util.ACacheUtil;
import com.gusteauscuter.youyanguan.util.CalendarUtil;
import com.gusteauscuter.youyanguan.util.HttpUtil;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.util.SharedPreferencesUtil;
import com.gusteauscuter.youyanguan.view.XImageView;

import java.net.SocketTimeoutException;
import java.util.List;


public class BookBorrowedAdapter extends ZBaseAdapter {

    ACacheUtil mCache ;

    public BookBorrowedAdapter(Context context){
        super(context);
        mCache = ACacheUtil.get(mContext);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {

        final ViewHolder mHolder;
        if (convertView == null) {
            convertView=mLayoutInflater.inflate(R.layout.card_book,container, false);
            mHolder =new ViewHolder();
            mHolder.mButtonBorrow=(TextView) convertView.findViewById(R.id.button_Borrow);
            mHolder.mBookPicture=(XImageView) convertView.findViewById(R.id.BookPicture);
            mHolder.mName=((TextView) convertView.findViewById(R.id.text_Title));
            mHolder.mBorrowDay=((TextView) convertView.findViewById(R.id.text_BorrowDay));
            mHolder.mReturnDay=(TextView) convertView.findViewById(R.id.text_ReturnDay);
            mHolder.mBorrowedTime=((TextView) convertView.findViewById(R.id.text_BorrowedTime));
            convertView.setTag(mHolder);
        } else{
            mHolder=(ViewHolder) convertView.getTag();
        }

        final BookBorrowed mBook = (BookBorrowed) mItemList.get(position);
        mHolder.mButtonBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBook.getBorrowedTime() >= mBook.getMaxBorrowTime())
                    Toast.makeText(mContext, "已达最大续借次数，请及时归还", Toast.LENGTH_SHORT).show();
                if(NetworkConnectUtil.isConnected(mContext)){
                    new RenewBookAsy(mBook.getBookId(),position).execute();
                }
            }
        });

        mHolder.mBookPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isConnected = NetworkConnectUtil.isConnected(mContext);
                if(isConnected){
                    Intent intent =new Intent(mContext, BookDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(PublicString.BUNDLE_BookId, mBook.getBookId());
                    bundle.putString(PublicString.BUNDLE_Title, mBook.getTitle());
                    bundle.putInt(PublicString.BUNDLE_Position, position);
                    bundle.putBoolean(PublicString.BUNDLE_IsFromBase, false);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        });

        // TO 设置Book对应属性
        String name=mBook.getTitle();
        String borrowDay="借阅:"+mBook.getBorrowDay();
        String returnDay="归还:"+mBook.getReturnDay();
        String borrowedTime="续借次数:"+  mBook.getBorrowedTime()+"/"+ mBook.getMaxBorrowTime();

        mHolder.mName.setText( name);
        mHolder.mBorrowDay.setText(borrowDay);
        mHolder.mReturnDay.setText(returnDay);
        mHolder.mBorrowedTime.setText(borrowedTime);

        int[] book_color={
                mContext.getResources().getColor(R.color.book_color_1),
                mContext.getResources().getColor(R.color.book_color_2),
                mContext.getResources().getColor(R.color.book_color_3),
                mContext.getResources().getColor(R.color.book_color_4),
                mContext.getResources().getColor(R.color.book_color_5),
                mContext.getResources().getColor(R.color.book_color_6),
                mContext.getResources().getColor(R.color.book_color_7),
        };

        //设置图片

        int no =(position)%book_color.length;
        int color = book_color[no];
        mHolder.setColor(color);
        String bookId = mBook.getBookId();
        Bitmap bitmap = mCache.getAsBitmap(bookId);
        if(bitmap==null){
            new GetBookIconByUrlAsy(mBook.getBookId(),mBook.getIconUrl(),mHolder)
                    .execute();
        }else{
            mHolder.setImage(bitmap);
        }
        return convertView;
    }

    public final class ViewHolder{
        public TextView mButtonBorrow;
        public XImageView mBookPicture;
        public TextView mName;
        public TextView mBorrowDay;
        public TextView mReturnDay;
        public TextView mBorrowedTime;

        public void setColor(int color){
            mBookPicture.setBackgroundColor(color);
            mBookPicture.setImageAlpha(0);
            mName.setVisibility(View.VISIBLE);
        }
        public void setImage(@Nullable Bitmap bitmap){
            mBookPicture.setImageBitmap(bitmap);
            mBookPicture.setImageAlpha(255);
            mBookPicture.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mName.setVisibility(View.GONE);
        }
    }


    private class RenewBookAsy extends AsyncTask<Void, Void, BookBorrowed> {
        private String mBookId;
        private int mPosition;
        public RenewBookAsy(String bookId, int position) {
            mBookId=bookId;
            mPosition=position;
        }

        @Override
        protected BookBorrowed doInBackground(Void... args) {
            BookBorrowed renewedBook = null;
            try {
                SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(mContext);
                String username = sharedPreferencesUtil.getUSERNAME();
                String password = sharedPreferencesUtil.getPASSWORD();
                InternetServiceApi internetService = new InternetServiceApiImpl();
                boolean result = false;
                if (internetService.Login(username,password))
                    result=internetService.RenewBook(mBookId);
                if(result)
                    renewedBook = (BookBorrowed) mItemList.get(mPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return renewedBook;
        }

        @Override
        protected void onPostExecute(BookBorrowed renewedBook) {
            if (renewedBook == null){
                Toast.makeText(mContext, "本书尚未到续借时间", Toast.LENGTH_SHORT).show();
                return;
            }
            mItemList.set(mPosition,renewedBook);
            new CalendarUtil(mContext).new AddCalendarThread((List<BookBorrowed>) getItemList()).start();
            Toast.makeText(mContext, "续借成功" , Toast.LENGTH_SHORT).show();
        }
    }

    private class GetBookIconByUrlAsy extends AsyncTask<Void, Void, Bitmap>{
        private String bookId;
        private String url ;
        private ViewHolder viewHolder;

        public GetBookIconByUrlAsy(String bookId, String url, ViewHolder viewHolder) {
            this.bookId = bookId;
            this.url = url;
            this.viewHolder = viewHolder;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                return HttpUtil.getPicture(url);
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null){
                mCache.put(bookId,bitmap);
                viewHolder.setImage(bitmap);
            }
        }

    }
}
