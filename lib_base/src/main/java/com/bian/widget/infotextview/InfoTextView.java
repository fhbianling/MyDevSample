package com.bian.widget.infotextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
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

    //drawableLeft的标志以及添加位置
    public static final int INDEX_LEFT = 0;
    //drawableRight的标志以及添加位置
    public static final int INDEX_RIGHT = -1;
    private LinearLayout root;

    private TextView mHintTv;

    private View mInfoView;

    private View mDrawableLeft;

    private View mDrawableRight;

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

    /**
     * 是否在指定宽度的条件下，自动调整字体间距的TextView,用于使hint部分对齐
     * 类似下面的效果
     * hint1hint1:info1
     * h i n t  2:info2
     * todo 当前该功能未完成
     */
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
    private OnCheckedChangeListener mOnCheckedChangeListener;
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
            if (v.equals(mDrawableLeft)) {
                mOnDrawableClickListener.onDrawableLeftClick(InfoTextView.this, mDrawableLeft);
            } else if (v.equals(mDrawableRight)) {
                mOnDrawableClickListener.onDrawableRightClick(InfoTextView.this, mDrawableRight);
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener mInnerCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChange(InfoTextView.this, isChecked());
            }
        }
    };

    public InfoTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        assignmentField(context, attrs);
        addRoot();
        addDivider();
    }

    /**
     * 设置HintView的文字
     */
    public void setHintText(CharSequence hint) {
        mHintText = hint;
        if (mHintTv != null) {
            mHintTv.setText(mHintText);
        }
    }

    /**
     * 获得InfoView的文字
     *
     * @return 当InfoView的type为图像时返回null, 其他时候返回文字
     */
    public @Nullable
    CharSequence getInfoText() {
        if (mInfoView == null || !(mInfoView instanceof TextView)) return null;
        return ((TextView) mInfoView).getText();
    }

    /**
     * 设置InfoView的文字,当InfoView为图像时会扔异常
     */
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
            throw new UnsupportedOperationException(
                    "wrong infoView type,infoView isn't instance of TextView");
        }
    }

    /**
     * 获得InfoView，根据设置的infoViewType,可能为TextView,EditText或者ImageView,
     * 当为ImageView时，可以通过该种方式拿到ImageView进行图片加载
     *
     * @return 以View的形式返回的InfoView, 当提供的xml属性和方法不足以进行更详细的设置时，可以通过拿到InfoView进行更详细的设置
     */
    public View getInfoView() {
        return mInfoView;
    }

    /**
     * 仅在左右drawable其一为checkable type时可以调用该方法，否则会抛异常
     *
     * @return 是否选中
     */
    @Override
    public boolean isChecked() {
        checkableStateValid();
        return getCheckableDrawable().isChecked();
    }

    /**
     * 仅在左右drawable其一为checkable type时可以调用该方法，否则会抛异常
     * <p>
     * 设置是否选中
     */
    @Override
    public void setChecked(boolean checked) {
        checkableStateValid();
        getCheckableDrawable().setChecked(checked);
    }

    /**
     * 仅在左右drawable其一为checkable type时可以调用该方法，否则会抛异常
     * <p>
     * 选中状态取反
     */
    @Override
    public void toggle() {
        checkableStateValid();
        getCheckableDrawable().toggle();
    }

    /**
     * 设置InfoView的点击监听器
     */
    public void setOnInfoViewClickListener(OnInfoViewClickListener value) {
        mOnInfoViewClickListener = value;

        if (!(mInfoView instanceof EditText) && mOnInfoViewClickListener != null) {
            mInfoView.setOnClickListener(mOnViewClickListener);
        } else {
            mInfoView.setOnClickListener(null);
        }
    }

    /**
     * 设置InfoView左右的drawable点击监听器
     */
    public void setOnDrawableClickListener(OnDrawableClickListener value) {
        this.mOnDrawableClickListener = value;
        OnClickListener onClickListener = mOnDrawableClickListener != null ? mOnViewClickListener : null;
        if (mDrawableLeft != null) {
            mDrawableLeft.setOnClickListener(onClickListener);
        }
        if (mDrawableRight != null) {
            mDrawableRight.setOnClickListener(onClickListener);
        }
    }

    /**
     * 设置选中状态监听器，仅在左右drawable其一为checkable type时可以调用该方法，否则会抛异常
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener value) {
        checkableStateValid();
        this.mOnCheckedChangeListener = value;
    }

    private void addRoot() {
        root = new LinearLayout(getContext());
        root.setGravity(Const.findInfoRootGravity(mInfoRootGravity));
        root.setPadding(mPaddingLeft, 0, mPaddingRight, 0);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        addView(root, lp);

        addHint();
        addInfo();

        if (mDrawableRightType == mDrawableLeftType && mDrawableLeftType == Const.DRAWABLE_TYPE_CHECKABLE) {
            throw new IllegalArgumentException("can only have one checkable drawable");
        }

        addRootDrawable(INDEX_LEFT);
        addRootDrawable(INDEX_RIGHT);
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

    private void addRootDrawable(int index) {
        View drawable = null;
        boolean isLeftDrawable = index == INDEX_LEFT;
        int drawableRes = isLeftDrawable ? mDrawableLeftRes : mDrawableRightRes;
        int drawableType = isLeftDrawable ? mDrawableLeftType : mDrawableRightType;
        int drawableSideLength = isLeftDrawable ? mDrawableLeftSideLength : mDrawableRightSideLength;
        int drawablePadding = isLeftDrawable ? mDrawableLeftPadding : mDrawableRightPadding;

        if (drawableType == Const.DRAWABLE_TYPE_CHECKABLE) {
            drawable = new CheckBox(getContext());
            if (drawableRes != DefaultConfig.NONE) {
                ((CheckBox) drawable).setButtonDrawable(null);
                drawable.setBackgroundResource(drawableRes);
            }
            ((CheckBox) drawable).setOnCheckedChangeListener(mInnerCheckedChangedListener);
        } else if (drawableRes != DefaultConfig.NONE) {
            drawable = new ImageView(getContext());
            ((ImageView) drawable).setImageResource(drawableRes);
        }

        if (drawable == null) return;

        int width = getWidthOfLayoutParams(drawableSideLength);
        //noinspection SuspiciousNameCombination
        LayoutParams lp = new LayoutParams(width, width);

        if (isLeftDrawable) {
            lp.rightMargin = drawablePadding;
            mDrawableLeft = drawable;
        } else {
            lp.leftMargin = drawablePadding;
            mDrawableRight = drawable;
        }

        root.addView(drawable, index, lp);
    }

    private int getWidthOfLayoutParams(int width) {
        return width != DefaultConfig.WARP_CONTENT ? width : ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private void addInfo() {
        switch (mInfoViewType) {
            case Const.INFO_TYPE_IMAGE_VIEW:
                addInfoImageView();
                break;
            case Const.INFO_TYPE_TEXT_VIEW:
            case Const.INFO_TYPE_EDIT_TEXT:
                addInfoTVOrET();
                break;
        }

        if (mInfoBackgroundDrawableRes != DefaultConfig.NONE && mInfoView != null) {
            mInfoView.setBackgroundResource(mInfoBackgroundDrawableRes);
        }
    }

    private void addInfoTVOrET() {
        if (mInfoViewType == Const.INFO_TYPE_EDIT_TEXT) {
            mInfoView = new EditText(getContext());
        } else {
            mInfoView = new TextView(getContext());
        }

        LayoutParams layoutParams = new LayoutParams(0,
                                                     getInfoViewHeight(), 1);
        layoutParams.leftMargin = mInfoPadding;
        layoutParams.topMargin = mInfoViewTopMargin;
        layoutParams.bottomMargin = mInfoViewBottomMargin;

        root.addView(mInfoView, layoutParams);

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

    private void addInfoImageView() {
        mInfoView = new ImageView(getContext());
        Space placeHolderSpace = new Space(getContext());
        root.addView(placeHolderSpace, new LayoutParams(0, 1, 1));
        int width = getWidthOfLayoutParams(mInfoImageViewSideLength);
        //infoPadding属性在该种模式下无效，图片始终被添加到右侧
        //noinspection SuspiciousNameCombination
        root.addView(mInfoView, width, width);

        //fix view attr
        ImageView mImg = (ImageView) mInfoView;
        mImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (mInfoImageViewSrc != DefaultConfig.NONE) {
            mImg.setImageResource(mInfoImageViewSrc);
        }
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

    private void addHint() {
        mHintTv = new LetterSpacingTextView(getContext());
        ((LetterSpacingTextView) mHintTv).setAutoAdjustLetterSpacing(mAutoAdjustHintLetterSpacing);
        mHintTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHintSize);
        mHintTv.setTextColor(mHintColor);
        mHintTv.setGravity(Gravity.CENTER_VERTICAL);
        int width = getWidthOfLayoutParams(mHintWidth);
        root.addView(mHintTv, width, LayoutParams.WRAP_CONTENT);
        if (mHintBackgroundDrawableRes != DefaultConfig.NONE) {
            mHintTv.setBackgroundResource(mHintBackgroundDrawableRes);
        }

        setHintText(mHintText);
    }

    private void assignmentField(Context context, AttributeSet attrs) {
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

    private CheckBox getCheckableDrawable() {
        return (CheckBox) (isLeftCheckable() ? mDrawableLeft : mDrawableRight);
    }

    private void checkableStateValid() {
        if (!isCheckDrawableStyle()) {
            throw new UnsupportedOperationException(
                    "this InfoTextView hasn't specified checkable drawable");
        }
    }

    private boolean isCheckDrawableStyle() {
        return mDrawableRightType == Const.DRAWABLE_TYPE_CHECKABLE || mDrawableLeftType == Const.DRAWABLE_TYPE_CHECKABLE;
    }

    private boolean isLeftCheckable() {
        return mDrawableLeftType == Const.DRAWABLE_TYPE_CHECKABLE;
    }

    public interface OnInfoViewClickListener {
        void onClick(InfoTextView infoTextView, View infoView);
    }

    public interface OnDrawableClickListener {
        void onDrawableLeftClick(InfoTextView infoTextView, View drawableLeft);

        void onDrawableRightClick(InfoTextView infoTextView, View drawableRight);
    }


    public interface OnCheckedChangeListener {
        void onCheckedChange(InfoTextView infoTextView, boolean isCheck);
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
