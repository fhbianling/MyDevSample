package com.bian.base.widget.info;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bian.base.R;

/**
 * author 边凌
 * date 2017/11/22 15:11
 * 类描述:对常见的  用户名：XXXX这种类型的布局进行封装
 * <p>
 * hint:提示信息，标题（用户名）
 * info:具体信息（XXXX）
 * 在xml中可以对hint,info的字体大小，颜色，文字进行设置
 * <p>
 * 样式目前设计成只能在xml中调整，但文字提供java方法用于调整
 */

public class InfoTextView extends LinearLayout implements Checkable {

    private LinearLayout infoRoot;

    private TextView mHintTv;

    private View mInfoView;

    private View mLeftIV;

    private View mRightIV;

    private Space placeHolderSpace;

    /**
     * HintView文字内容
     */
    private CharSequence mHintText;

    /**
     * InfoView为TextView或EditText形式时的 hint内容
     */
    private String mInfoHintText;

    /**
     * InfoView为TextView或EditText形式时的 text内容
     */
    private CharSequence mInfoText;

    /**
     * HintView文字颜色
     */
    private @ColorInt
    int mHintColor;

    /**
     * InfoView为TextView或EditText形式时的 text字色
     */
    private @ColorInt
    int mInfoTextColor;

    /**
     * InfoView为TextView或EditText形式时的 hint字色
     */
    private @ColorInt
    int mInfoHintColor;

    /**
     * HintView字号
     */
    private int mHintSize;

    /**
     * InfoView为TextView或EditText形式时的字号
     */
    private int mInfoTextViewSize;

    /**
     * InfoView的类型(EditText,TextView,ImageView)
     */
    private int mInfoViewType;

    /**
     * InfoView与HintView的间距
     */
    private int mInfoPadding;

    /**
     * HintView的宽度，默认为wrap_content
     */
    private int mHintWidth;

    /**
     * InfoView为ImageView形式时的边长
     */
    private int mInfoImageViewSideLength;

    /**
     * InfoView为EditText形式时的InputType
     */
    private int mInfoEditTextInputType;

    /**
     * 该属性为true时，如果info信息为空，则自动隐藏整个布局
     */
    private boolean mGoneIfInfoEmpty;

    /**
     * 控制InfoView 文字的gravity
     */
    private int mInfoTextGravity;

    /**
     * 当InfoView为ImageView形式时的资源文件
     */
    private @DrawableRes
    int mInfoImageViewSrc;

    /**
     * InfoView的background 的drawableRes
     */
    private @DrawableRes
    int mInfoBackgroundDrawableRes;

    /**
     * InfoView为TextView(包括EditText)时是否限制单行
     */
    private boolean mInfoViewSingleLine;

    /**
     * HintView的background 的drawableRes
     */
    private @DrawableRes
    int mHintBackgroundDrawableRes;

    //是否在指定宽度的条件下，自动调整字体间距的TextView,用于使hint部分对齐
    //类似下面的效果
    //hint1hint1:info1
    //h i n t  2:info2
    //todo 当前该功能未完成
    private boolean mAutoAdjustHintLetterSpacing;

    /**
     * drawableLeft view 的边长
     */
    private int mDrawableLeftSideLength;

    /**
     * drawableLeft与HintView的间距
     */
    private int mDrawableLeftPadding;

    /**
     * drawableLeft引用的资源
     */
    private int mDrawableLeftRes;

    /**
     * drawableRight边长
     */
    private int mDrawableRightSideLength;

    /**
     * drawableRight与InfoView的间距
     */
    private int mDrawableRightPadding;

    /**
     * drawableRight引用的资源
     */
    private int mDrawableRightRes;

    /**
     * 顶部分割线颜色
     */
    private int mDividerTopColor;

    /**
     * 顶部分割线左边距
     */
    private int mDividerTopPaddingLeft;

    /**
     * 顶部分割线右边距
     */
    private int mDividerTopPaddingRight;

    /**
     * 顶部分割线高度
     */
    private int mDividerTopHeight;

    /**
     * 顶部分割线
     */
    private int mDividerBottomColor;

    /**
     * 顶部分割线左边距
     */
    private int mDividerBottomPaddingLeft;

    /**
     * 顶部分割线右边距
     */
    private int mDividerBottomPaddingRight;

    /**
     * 顶部分割线高度
     */
    private int mDividerBottomHeight;

    /**
     * 整个InfoView和HintView所在的LinearLayout的gravity
     */
    private int mInfoRootGravity;

    /**
     * InfoRoot的paddingLeft
     */
    private int mPaddingLeft;

    /**
     * InfoRoot的paddingRight
     */
    private int mPaddingRight;

    /**
     * InfoView的paddingLeft
     */
    private int mInfoViewLeftPadding;

    /**
     * InfoView的paddingRight
     */
    private int mInfoViewRightPadding;

    /**
     * InfoView的topMargin
     */
    private int mInfoViewTopMargin;

    /**
     * InfoView的bottomMargin
     */
    private int mInfoViewBottomMargin;

    /**
     * InfoView的高度
     */
    private int mInfoViewHeight;

    /**
     * DrawableLeft 的类型：图片或可选择的图片控件
     */
    private int mDrawableLeftType;

    /**
     * DrawableRight 的类型，图片或可选择的图片控件
     */
    private int mDrawableRightType;

    private OnInfoViewClickListener mOnInfoViewClickListener;
    private OnDrawableClickListener mOnDrawableClickListener;
    private OnClickListener mOnViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(mInfoView) && mOnInfoViewClickListener != null) {
                mOnInfoViewClickListener.onClick(InfoTextView.this, mInfoView);
                return;
            }

            if (mOnDrawableClickListener == null) {
                return;
            }
            if (v.equals(mLeftIV)) {
                mOnDrawableClickListener.onDrawableLeftClick(InfoTextView.this, mLeftIV);
            } else if (v.equals(mRightIV)) {
                mOnDrawableClickListener.onDrawableRightClick(InfoTextView.this, mRightIV);
            }
        }
    };

    public InfoTextView(Context context,
                        @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setHintText(CharSequence hint) {
        mHintText = hint;
        if (mHintTv != null) {
            mHintTv.setText(mHintText);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        initAttr(context, attrs);
        addInfoRoot();
        addDivider();
    }

    private void addInfoRoot() {
        infoRoot = new LinearLayout(getContext());
        infoRoot.setGravity(Const.findInfoRootGravity(mInfoRootGravity));
        infoRoot.setPadding(mPaddingLeft, 0, mPaddingRight, 0);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        addView(infoRoot, lp);
        addHintView();
        addInfoView();
        addInfoRootDrawable();
    }

    private void addDivider() {
        if (mDividerTopColor != DefaultConfig.NONE) {
            View view = new View(getContext());
            view.setBackgroundColor(mDividerTopColor);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, mDividerTopHeight);
            lp.leftMargin = mDividerTopPaddingLeft;
            lp.rightMargin = mDividerTopPaddingRight;
            addView(view, 0, lp);
        }

        if (mDividerBottomColor != DefaultConfig.NONE) {
            View view = new View(getContext());
            view.setBackgroundColor(mDividerBottomColor);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, mDividerBottomHeight);
            lp.leftMargin = mDividerBottomPaddingLeft;
            lp.rightMargin = mDividerBottomPaddingRight;
            addView(view, lp);
        }

    }

    private void addInfoRootDrawable() {
        if (mDrawableRightType == mDrawableLeftType && mDrawableLeftType == Const.DRAWABLE_TYPE_CHECKABLE) {
            throw new IllegalArgumentException("左右Drawable不能同时为Checkable类型");
        }

        if (mDrawableLeftRes != DefaultConfig.NONE) {
            if (mDrawableLeftType == Const.DRAWABLE_TYPE_CHECKABLE) {
                mLeftIV = new CheckBox(getContext());
                mLeftIV.setBackgroundResource(mDrawableLeftRes);
            } else {
                mLeftIV = new ImageView(getContext());
                ((ImageView) mLeftIV).setImageResource(mDrawableLeftRes);
            }
            int width = mDrawableLeftSideLength != DefaultConfig.WARP_CONTENT ? mDrawableLeftSideLength : LayoutParams.WRAP_CONTENT;
            //noinspection SuspiciousNameCombination
            LayoutParams lp = new LayoutParams(width, width);
            lp.rightMargin = mDrawableLeftPadding;
            infoRoot.addView(mLeftIV, 0, lp);
        }

        if (mDrawableRightRes != DefaultConfig.NONE) {
            if (mDrawableRightType == Const.DRAWABLE_TYPE_CHECKABLE) {
                mRightIV = new CheckBox(getContext());
                mRightIV.setBackgroundResource(mDrawableRightRes);
            } else {
                mRightIV = new ImageView(getContext());
                ((ImageView) mLeftIV).setImageResource(mDrawableRightRes);
            }
            int width = mDrawableRightSideLength != DefaultConfig.WARP_CONTENT ? mDrawableRightSideLength : LayoutParams.WRAP_CONTENT;
            //noinspection SuspiciousNameCombination
            LayoutParams lp = new LayoutParams(width, width);
            lp.leftMargin = mDrawableRightPadding;
            infoRoot.addView(mRightIV, lp);
        }
    }

    private void addInfoView() {
        int indexOfChild = -1;
        if (placeHolderSpace != null) {
            removeView(placeHolderSpace);
        }
        if (mInfoView != null) {
            indexOfChild = indexOfChild(mInfoView);
            removeView(mInfoView);
        }

        switch (mInfoViewType) {
            case Const.INFO_TYPE_IMAGE_VIEW:
                addInfoImageView(indexOfChild);
                break;
            case Const.INFO_TYPE_TEXT_VIEW:
            case Const.INFO_TYPE_EDIT_TEXT:
                addInfoTVOrET(indexOfChild);
                break;
        }

        if (mInfoBackgroundDrawableRes != DefaultConfig.NONE && mInfoView != null) {
            mInfoView.setBackgroundResource(mInfoBackgroundDrawableRes);
        }

    }

    private void addInfoTVOrET(int indexOfChild) {
        if (mInfoViewType == Const.INFO_TYPE_EDIT_TEXT) {
            mInfoView = new EditText(getContext());
        } else {
            mInfoView = new TextView(getContext());
        }
        LayoutParams layoutParams = getInfoTvLp();
        infoRoot.addView(mInfoView, indexOfChild, layoutParams);

        int gravityValue = Const.transformToViewGravity(mInfoTextGravity);

        if (mInfoViewSingleLine) {
            ((TextView) mInfoView).setMaxLines(1);
            ((TextView) mInfoView).setEllipsize(TextUtils.TruncateAt.END);
            ((TextView) mInfoView).setSingleLine(true);
            ((TextView) mInfoView).setHorizontallyScrolling(true);
            ((TextView) mInfoView).setGravity(gravityValue | Gravity.CENTER_VERTICAL);
        } else {
            ((TextView) mInfoView).setGravity(gravityValue);
        }

        //fix view attr
        TextView mInfoTv = (TextView) mInfoView;
        mInfoTv.setHint(mInfoHintText);
        mInfoTv.setTextColor(mInfoTextColor);
        mInfoTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mInfoTextViewSize);
        mInfoTv.setHintTextColor(mInfoHintColor);
        mInfoTv.setBackground(null);
        mInfoTv.setPadding(mInfoViewLeftPadding, 0, mInfoViewRightPadding, 0);

        //针对密码输入类型，setInputType须在singleLine后才有效
        if (mInfoView instanceof EditText) {
            ((EditText) mInfoView).setInputType(Const.findInputType(
                    mInfoEditTextInputType));
        }
        setInfoText(mInfoText);
    }

    private void addInfoImageView(int indexOfChild) {
        mInfoView = new ImageView(getContext());
        if (placeHolderSpace == null) {
            placeHolderSpace = new Space(getContext());
        }
        infoRoot.addView(placeHolderSpace, indexOfChild, new LayoutParams(0, 1, 1));
        int width = mInfoImageViewSideLength == DefaultConfig.WARP_CONTENT ? ViewGroup.LayoutParams.WRAP_CONTENT : mInfoImageViewSideLength;
        //infoPadding属性在该种模式下无效，图片始终被添加到右侧
        //noinspection SuspiciousNameCombination
        infoRoot.addView(mInfoView, width, width);

        //fix view attr
        ImageView mImg = (ImageView) mInfoView;
        mImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (mInfoImageViewSrc != DefaultConfig.NONE) {
            mImg.setImageResource(mInfoImageViewSrc);
        }
    }

    @NonNull
    private LayoutParams getInfoTvLp() {
        LayoutParams layoutParams = new LayoutParams(0,
                                                     getInfoViewHeight(), 1);
        layoutParams.leftMargin = mInfoPadding;
        layoutParams.topMargin = mInfoViewTopMargin;
        layoutParams.bottomMargin = mInfoViewBottomMargin;
        return layoutParams;
    }

    private int getInfoViewHeight() {
        measure(0, 0);
        int measuredHeight = getMeasuredHeight();
        int height;
        if (measuredHeight == 0) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            height = mInfoViewHeight == DefaultConfig.MATCH_PARENT ? ViewGroup.LayoutParams.MATCH_PARENT : mInfoViewHeight;
        }
        return height;
    }

    public @Nullable
    CharSequence getInfoText() {
        if (mInfoView == null || !(mInfoView instanceof TextView)) return null;
        return ((TextView) mInfoView).getText();
    }

    public void setInfoText(CharSequence info) {
        mInfoText = info;
        if (mInfoView != null && mInfoView instanceof TextView) {
            ((TextView) mInfoView).setText(mInfoText);
            if (mGoneIfInfoEmpty && TextUtils.isEmpty(mInfoText)) {
                setVisibility(GONE);
            } else {
                setVisibility(VISIBLE);
            }
        } else {
            Log.e("InfoTextView", "wrong infoView type,infoView isn't instance of TextView");
        }
    }

    public View getInfoView() {
        return mInfoView;
    }

    private void addHintView() {
        mHintTv = new LetterSpacingTextView(getContext());
        ((LetterSpacingTextView) mHintTv).setAutoAdjustLetterSpacing(mAutoAdjustHintLetterSpacing);
        mHintTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHintSize);
        mHintTv.setTextColor(mHintColor);
        mHintTv.setGravity(Gravity.CENTER_VERTICAL);
        int width = mHintWidth == DefaultConfig.WARP_CONTENT ? LayoutParams.WRAP_CONTENT : mHintWidth;
        infoRoot.addView(mHintTv, width, LayoutParams.WRAP_CONTENT);
        if (mHintBackgroundDrawableRes != DefaultConfig.NONE) {
            mHintTv.setBackgroundResource(mHintBackgroundDrawableRes);
        }

        setHintText(mHintText);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfoTextView);
        mHintText = typedArray.getString(R.styleable.InfoTextView_hintText);
        mInfoHintText = typedArray.getString(R.styleable.InfoTextView_infoHintText);
        mInfoText = typedArray.getString(R.styleable.InfoTextView_infoText);
        mHintSize = typedArray.getDimensionPixelSize(R.styleable.InfoTextView_hintSize,
                                                     DefaultConfig.TEXT_SIZE);
        mHintColor = typedArray.getColor(R.styleable.InfoTextView_hintColor,
                                         DefaultConfig.TEXT_COLOR);
        mInfoTextViewSize = typedArray.getDimensionPixelSize(R.styleable.InfoTextView_infoTextViewSize,
                                                             DefaultConfig.TEXT_SIZE);
        mInfoTextColor = typedArray.getColor(R.styleable.InfoTextView_infoTextColor,
                                             DefaultConfig.TEXT_COLOR);
        mInfoHintColor = typedArray.getColor(R.styleable.InfoTextView_infoHintColor,
                                             DefaultConfig.INFO_HINT_COLOR);
        mInfoPadding = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_infoPadding,
                                                          DefaultConfig.INFO_PADDING);
        mHintWidth = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_hintWidth,
                                                        DefaultConfig.WARP_CONTENT);
        mInfoImageViewSideLength = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_infoImageViewSideLength,
                                                                      DefaultConfig.WARP_CONTENT);
        mGoneIfInfoEmpty = typedArray.getBoolean(R.styleable.InfoTextView_goneIfInfoEmpty,
                                                 DefaultConfig.GONE_IF_INFO_EMPTY);
        mInfoTextGravity = typedArray.getInt(R.styleable.InfoTextView_infoTextGravity,
                                             DefaultConfig.INFO_TEXT_GRAVITY);
        mAutoAdjustHintLetterSpacing = typedArray.getBoolean(R.styleable.InfoTextView_autoAdjustHintLetterSpacing,
                                                             DefaultConfig.ADJUST_LETTER_SPACING);
        mHintBackgroundDrawableRes = typedArray.getResourceId(R.styleable.InfoTextView_hintBackground,
                                                              DefaultConfig.NONE);
        mInfoBackgroundDrawableRes = typedArray.getResourceId(R.styleable.InfoTextView_infoBackground,
                                                              DefaultConfig.NONE);
        mInfoImageViewSrc = typedArray.getResourceId(R.styleable.InfoTextView_infoImageViewSrc,
                                                     DefaultConfig.NONE);
        mInfoViewType = typedArray.getInt(R.styleable.InfoTextView_infoViewType,
                                          DefaultConfig.INFO_VIEW_TYPE);
        mInfoViewSingleLine = typedArray.getBoolean(R.styleable.InfoTextView_infoViewSingleLine,
                                                    DefaultConfig.INFO_VIEW_SINGLE_LINE);
        mInfoEditTextInputType = typedArray.getInt(R.styleable.InfoTextView_infoEditTextInputType,
                                                   DefaultConfig.INFO_EDIT_TEXT_INPUT_TYPE);
        mDrawableLeftPadding = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_drawableLeftPadding,
                                                                  DefaultConfig.DRAWABLE_PADDING);
        mDrawableLeftSideLength = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_drawableLeftSideLength,
                                                                     DefaultConfig.WARP_CONTENT);
        mDrawableLeftRes = typedArray.getResourceId(R.styleable.InfoTextView_drawableLeft,
                                                    DefaultConfig.NONE);
        mDrawableRightPadding = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_drawableRightPadding,
                                                                   DefaultConfig.DRAWABLE_PADDING);
        mDrawableRightSideLength = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_drawableRightSideLength,
                                                                      DefaultConfig.WARP_CONTENT);
        mDrawableRightRes = typedArray.getResourceId(R.styleable.InfoTextView_drawableRight,
                                                     DefaultConfig.NONE);
        mDividerTopColor = typedArray.getColor(R.styleable.InfoTextView_dividerTopColor,
                                               DefaultConfig.NONE);
        mDividerTopPaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_dividerTopPaddingLeft,
                                                                    DefaultConfig.ZERO);
        mDividerTopPaddingRight = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_dividerTopPaddingRight,
                                                                     DefaultConfig.ZERO);
        mDividerTopHeight = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_dividerTopHeight,
                                                               DefaultConfig.DIVIDER_HEIGHT);
        mDividerBottomColor = typedArray.getColor(R.styleable.InfoTextView_dividerBottomColor,
                                                  DefaultConfig.NONE);
        mDividerBottomPaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_dividerBottomPaddingLeft,
                                                                       DefaultConfig.ZERO);
        mDividerBottomPaddingRight = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_dividerBottomPaddingRight,
                                                                        DefaultConfig.ZERO);
        mDividerBottomHeight = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_dividerBottomHeight,
                                                                  DefaultConfig.DIVIDER_HEIGHT);
        mInfoRootGravity = typedArray.getInt(R.styleable.InfoTextView_gravity,
                                             DefaultConfig.INFO_ROOT_GRAVITY);
        mPaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_paddingLeft,
                                                          DefaultConfig.ZERO);
        mPaddingRight = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_paddingRight,
                                                           DefaultConfig.ZERO);
        mInfoViewHeight = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_infoViewHeight,
                                                             DefaultConfig.MATCH_PARENT);
        mInfoViewTopMargin = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_infoViewTopMargin,
                                                                DefaultConfig.ZERO);
        mInfoViewBottomMargin = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_infoViewBottomMargin,
                                                                   DefaultConfig.ZERO);
        mInfoViewLeftPadding = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_infoViewLeftPadding,
                                                                  DefaultConfig.ZERO);
        mInfoViewRightPadding = typedArray.getDimensionPixelOffset(R.styleable.InfoTextView_infoViewRightPadding,
                                                                   DefaultConfig.ZERO);
        mDrawableLeftType = typedArray.getInt(R.styleable.InfoTextView_drawableLeftType,
                                              DefaultConfig.DRAWABLE_TYPE);
        mDrawableRightType = typedArray.getInt(R.styleable.InfoTextView_drawableRightType,
                                               DefaultConfig.DRAWABLE_TYPE);
        typedArray.recycle();
    }

    public void setOnInfoViewClickListener(OnInfoViewClickListener value) {
        mOnInfoViewClickListener = value;

        if (!(mInfoView instanceof EditText) && mOnInfoViewClickListener != null) {
            mInfoView.setOnClickListener(mOnViewClickListener);
        } else {
            mInfoView.setOnClickListener(null);
        }
    }

    public void setOnDrawableClickListener(OnDrawableClickListener value) {
        this.mOnDrawableClickListener = value;
        OnClickListener onClickListener = mOnDrawableClickListener != null ? mOnViewClickListener : null;
        if (mLeftIV != null) {
            mLeftIV.setOnClickListener(onClickListener);
        }
        if (mRightIV != null) {
            mRightIV.setOnClickListener(onClickListener);
        }
    }

    @Override
    public boolean isChecked() {
        checkableStateValid();
        // TODO: 2017/11/30  
        return false;
    }

    @Override
    public void setChecked(boolean checked) {
        checkableStateValid();
        // TODO: 2017/11/30  
    }

    private void checkableStateValid() {
        if (!isCheckDrawableStyle()) {
            throw new UnsupportedOperationException(
                    "该InfoTextView未指定可选状态的drawableLeft或drawableRight");
        }
    }

    private boolean isCheckDrawableStyle() {
        return mDrawableRightType == Const.DRAWABLE_TYPE_CHECKABLE || mDrawableLeftType == Const.DRAWABLE_TYPE_CHECKABLE;
    }

    @Override
    public void toggle() {
        checkableStateValid();
        // TODO: 2017/11/30  
    }

    public interface OnInfoViewClickListener {
        void onClick(InfoTextView infoTextView, View infoView);
    }

    public interface OnDrawableClickListener {
        void onDrawableLeftClick(InfoTextView infoTextView, View drawableLeft);

        void onDrawableRightClick(InfoTextView infoTextView, View drawableRight);
    }

    private final static class DefaultConfig {
        private static final int TEXT_COLOR = Color.parseColor("#333333");
        private static final int INFO_HINT_COLOR = Color.parseColor("#666666");
        private static final int WARP_CONTENT = -1;
        private static final int MATCH_PARENT = -2;
        private static final boolean ADJUST_LETTER_SPACING = false;
        private static final boolean GONE_IF_INFO_EMPTY = false;
        private static final boolean INFO_VIEW_SINGLE_LINE = true;
        private static final int INFO_PADDING;
        private static final int TEXT_SIZE;
        private static final int INFO_TEXT_GRAVITY = Const.INFO_TEXT_GRAVITY_RIGHT;
        private static final int INFO_VIEW_TYPE = Const.INFO_TYPE_TEXT_VIEW;
        private static final int NONE = -1;
        private static final int INFO_EDIT_TEXT_INPUT_TYPE = Const.INFO_INPUT_TYPE_TEXT;
        private static final int DRAWABLE_PADDING;
        private static final int ZERO = 0;
        private static final int DIVIDER_HEIGHT;
        private static final int INFO_ROOT_GRAVITY = Const.INFO_ROOT_GRAVITY_CENTER_VERTICAL;
        private static final int DRAWABLE_TYPE = Const.DRAWABLE_TYPE_IMAGE;

        static {
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            INFO_PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                           4,
                                                           displayMetrics);
            TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                                                        14,
                                                        displayMetrics);
            DIVIDER_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                             0.5f,
                                                             displayMetrics);
            DRAWABLE_PADDING = INFO_PADDING;
        }

    }

    @SuppressWarnings("WeakerAccess")
    public final static class Const {
        public final static int INFO_TEXT_GRAVITY_LEFT = 1;
        public final static int INFO_TEXT_GRAVITY_CENTER = 2;
        public final static int INFO_TEXT_GRAVITY_RIGHT = 3;

        public final static int INFO_TYPE_TEXT_VIEW = 1;
        public final static int INFO_TYPE_IMAGE_VIEW = 2;
        public final static int INFO_TYPE_EDIT_TEXT = 3;

        public final static int INFO_INPUT_TYPE_NUMBER = 1;
        public final static int INFO_INPUT_TYPE_NUMBER_DECIMAL = 2;
        public final static int INFO_INPUT_TYPE_NUMBER_PASSWORD = 3;
        public final static int INFO_INPUT_TYPE_TEXT_PASSWORD = 4;
        public final static int INFO_INPUT_TYPE_TEXT = 5;

        public final static int INFO_ROOT_GRAVITY_TOP = 1;
        public final static int INFO_ROOT_GRAVITY_CENTER_VERTICAL = 2;
        public final static int INFO_ROOT_GRAVITY_BOTTOM = 3;

        public final static int DRAWABLE_TYPE_IMAGE = 1;
        public final static int DRAWABLE_TYPE_CHECKABLE = 2;

        @SuppressLint("RtlHardcoded")
        private static int transformToViewGravity(int gravity) {
            int gravityValue = Gravity.LEFT;
            switch (gravity) {
                case Const.INFO_TEXT_GRAVITY_CENTER:
                    gravityValue = Gravity.CENTER;
                    break;
                case Const.INFO_TEXT_GRAVITY_LEFT:
                    gravityValue = Gravity.LEFT;
                    break;
                case Const.INFO_TEXT_GRAVITY_RIGHT:
                    gravityValue = Gravity.RIGHT;
                    break;
            }
            return gravityValue;
        }

        private static int findInputType(int inputType) {
            switch (inputType) {
                case INFO_INPUT_TYPE_NUMBER:
                    return InputType.TYPE_CLASS_NUMBER;
                case INFO_INPUT_TYPE_NUMBER_DECIMAL:
                    return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                case INFO_INPUT_TYPE_NUMBER_PASSWORD:
                    return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
                case INFO_INPUT_TYPE_TEXT_PASSWORD:
                    return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                default:
                    return InputType.TYPE_CLASS_TEXT;
            }
        }

        private static int findInfoRootGravity(int gravity) {
            switch (gravity) {
                case INFO_ROOT_GRAVITY_TOP:
                    return Gravity.TOP;
                case INFO_ROOT_GRAVITY_BOTTOM:
                    return Gravity.BOTTOM;
                default:
                    return Gravity.CENTER_VERTICAL;
            }
        }
    }
}
