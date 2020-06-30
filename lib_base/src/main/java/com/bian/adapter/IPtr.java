package com.bian.adapter;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * author 边凌
 * date 2018/1/11 15:08
 * 类描述：对于ListView和RecyclerView的上下拉刷新和数据加载逻辑提取共性抽成接口。
 * 由该接口的实现类来处理上述逻辑。
 *
 * @see IPtrImpl 实现类
 */

public interface IPtr {
    /**
     * 上拉加载更多
     * <p>
     * 当没有调用{@link #bindPtrLayout(PullToRefresh, PtrMode)}
     * 但重写了{@link Model#getDataLoader()}时，调用该方法依然会生效，但会导致数据重复
     */
    void refreshUp();

    /**
     * 下拉刷新
     * <p>
     * 当没有调用{@link #bindPtrLayout(PullToRefresh, PtrMode)}
     * 但重写了{@link Model#getDataLoader()}时，调用该方法依然会生效，且会刷新数据
     */
    void refreshDown();

    /**
     * 初次加载数据
     * <p>
     * 当没有调用{@link #bindPtrLayout(PullToRefresh, PtrMode)}
     * 但重写了{@link Model#getDataLoader()}时，调用该方法依然会生效，且会刷新数据
     */
    void firstLoad();

    /**
     * 重载数据
     * <p>
     * 当没有调用{@link #bindPtrLayout(PullToRefresh, PtrMode)}
     * 但重写了{@link Model#getDataLoader()}时，调用该方法依然会生效，且会刷新数据
     */
    void reload();

    /**
     * 获取当前页号，
     * 注意，这个页号并不对应于数据库页号，
     * 比如当前有3页，30条数据，每页10条，
     * 但随后进行了一次刷新操作，
     * 则此时的页号为1，但该页共有30条数据，
     * 在上述基础上，再进行一次加载更多操作时，会变成页号2，共40条数据，第一页30条，第二页10条
     */
    int getPageNum();

    /**
     * 设置数据加载监听器
     * 可以区别通过
     * {@link #firstLoad()}{@link #reload()}{@link #refreshUp()}{@link #refreshDown()}
     * 四个方法产生的数据加载操作，并对数据加载成功或失败进行回调
     */
    void setOnDataLoadListener(OnDataLoadListener onDataLoadListener);

    /**
     * 调用该方法以将适配器和上下拉刷新框架结合，在适配器实现了{@link Model#getDataLoader()}该方法的情况下,
     * 此时上下拉刷新的数据操作将由适配器进行处理
     *
     * @param pullToRefresh 相应ListView或RecyclerView的上下拉刷新容器
     * @param mode          上下拉刷新模式{@link PtrMode}
     */
    void bindPtrLayout(PullToRefresh pullToRefresh, PtrMode mode);

    /**
     * 设置每页默认大小
     * 注意该方法设置后并不会立即生效，而是在下次加载数据时生效
     *
     * @param defaultSize 默认大小
     */
    void setDefaultPageSize(int defaultSize);

    /**
     * 接口描述：数据加载类型枚举
     *
     * @see OnDataLoadListener#onLoadStart(LoadType) 在该回调方法中用于区别数据加载类型
     * 依序对应下拉刷新，上拉加载更多，重新加载，初次加载
     * 即{@link #refreshDown()}{@link #refreshUp()}{@link #reload()}{@link #firstLoad()}四个方法调用
     * <p>
     * {@link #Reload}与{@link #Refresh}在{@link IPtrImpl}的上述相应方法实现中并没有不同，
     * 但是不同的状态值可用于区别 例如，不同筛选条件和下拉刷新导致的两种刷新状况
     * 换句话说，{@link #Reload}主要是用于特定条件下数据刷新操作，而非下拉刷新。
     * <p>
     * 如果想让下述四个值正常生效，则第一次加载数据时应调用{@link #firstLoad()}，
     * 上下拉刷新交给{@link IPtr}和{@link PullToRefresh}的子类处理，同时在进行非下拉的刷新操作时调用{@link #reload()}
     */
    enum LoadType {
        Refresh,
        LoadMore,
        Reload,
        FirstLoad
    }

    enum PtrMode {Both, PullFromStart, PullFromEnd}

    /**
     * 接口描述：Adapter实现该接口以与{@link IPtr}的数据和控制层配合，实现上下拉刷新即数据加载的逻辑
     *
     * @see BasePtrAdapter
     * @see BaseRVPtrAdapter
     */
    interface Model<DataType> {
        /**
         * 在没有上下拉刷新的情况下，依然可以重写该方法，并通过调用{@link #firstLoad()}来实现数据的加载
         */
        DataLoader<DataType> getDataLoader();

        int getDataCount();

        void resetData(List<DataType> data);

        void addData(List<DataType> data);
    }

    /**
     * 接口描述：数据加载监听器，当该适配器被用于加载数据时，会在加载开始，失败，成功三种状态回调该接口方法
     */
    interface OnDataLoadListener {
        void onLoadSuccess(LoadType loadType, int pageNum);

        void onLoadFailed(LoadType loadType, int errorCode, @Nullable String msg);

        void onLoadStart(LoadType type);
    }

    /**
     * 接口描述：对于可能会用到的上下拉刷新框架，实现该接口用于和{@link IPtr}的子类配合实现上下拉刷新数据
     */
    interface PullToRefresh {
        /**
         * 该接口的子类需要满足可以上拉，下拉，上下拉都可这三种形式的刷新类型
         * 实现该方法以对各种mode做出支持
         */
        void setPtrMode(PtrMode mode);

        /**
         * 实现该方法，当该方法被调用时需要结束上拉和下拉刷新的UI效果
         */
        void onRefreshComplete();

        /**
         * 对于该处传入的listener,需要在UI上拉或下拉时，
         * 相应调用该listener的{@link OnRefreshListener#onRefreshUp()}和{@link OnRefreshListener#onRefreshDown()}两个方法
         * 换句话说，实现该方法将上下拉刷新UI框架的各种监听器，统一再由传入的{@link OnRefreshListener}实现一遍
         */
        void setOnRefreshListener(OnRefreshListener onRefreshListener);

    }

    /**
     * 接口描述：该监听器需要{@link PullToRefresh}来实现逻辑
     */
    interface OnRefreshListener {
        void onRefreshUp();

        void onRefreshDown();
    }

    /**
     * 接口描述：用于在数据加载后进行数据设置，
     * 这个接口的实现类会在
     * {@link DataLoader#loadData(int, int, DataSetter, LoadType)}这个重写方法被调用时，
     * 由{@link IPtr}内部创建并传入
     */
    interface DataSetter<T> {

        /**
         * 在数据加载后调用该方法传入数据，这里的数据应该是追加数据而非总数据
         */
        void setLoadedData(List<T> data);

        /**
         * 当发生错误时调用该方法，这里传入的错误信息会在{@link OnDataLoadListener#onLoadFailed(int, String)}该方法中被传入
         */
        void setFailed(int errorCode, @Nullable String dataErrorMsg);
    }

    /**
     * 接口描述：数据加载器，{@link Model#getDataLoader()}该方法重写时需要传入该接口的实现类
     * 加载成功后,方法参数{@link DataSetter}是一个Adapter的内部实现类对象的引用，不应对这个setter引用作任何重新赋值的操作
     * <p>
     * 调用其方法{@link DataSetter#setLoadedData(List)}或{@link DataSetter#setFailed(int, String)}设置数据
     */
    interface DataLoader<DataType> {

        /**
         * 该方法会被{@link IPtr}的实现类在做出各种数据加载操作时所调用以加载数据
         *
         * @param pageNum  页号,默认从1开始（根据接口有时可能为1，此时须在重写时对该值减1）
         * @param pageSize 每页大小，默认为10，
         *                 {@link #setDefaultPageSize(int)}该方法可设置，
         *                 注意该方法设置后并不会立即生效，而是在下次加载数据时生效
         * @param setter   setter的引用由{@link IPtr}内部创建并传入，数据加载成功或失败后，应通过setter来设置数据或错误信息
         * @param loadType 加载数据的加载类型{@link LoadType}
         */
        void loadData(@IntRange(from = 1) int pageNum, int pageSize,
                      final DataSetter<DataType> setter,
                      LoadType loadType);
    }
}
