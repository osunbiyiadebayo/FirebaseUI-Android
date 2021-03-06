package com.firebase.ui.firestore;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryListenOptions;

import static com.firebase.ui.common.Preconditions.assertNonNull;
import static com.firebase.ui.common.Preconditions.assertNull;

/**
 * Options to configure an {@link FirestoreRecyclerAdapter}.
 *
 * @see Builder
 */
public class FirestoreRecyclerOptions<T> {

    private static final String ERR_SNAPSHOTS_SET = "Snapshot array already set. " +
            "Call only one of setSnapshotArray or setQuery";
    private static final String ERR_SNAPSHOTS_NULL = "Snapshot array cannot be null. " +
            "Call one of setSnapshotArray or setQuery";

    private ObservableSnapshotArray<T> mSnapshots;
    private LifecycleOwner mOwner;

    private FirestoreRecyclerOptions(ObservableSnapshotArray<T> snapshots,
                                     @Nullable LifecycleOwner owner) {
        mSnapshots = snapshots;
        mOwner = owner;
    }

    /**
     * Get the {@link ObservableSnapshotArray} to observe.
     */
    @NonNull
    public ObservableSnapshotArray<T> getSnapshots() {
        return mSnapshots;
    }

    /**
     * Get the (optional) {@link LifecycleOwner}.
     */
    @Nullable
    public LifecycleOwner getOwner() {
        return mOwner;
    }

    /**
     * Builder for {@link FirestoreRecyclerOptions}.
     *
     * @param <T> the model class for the {@link FirestoreRecyclerAdapter}.
     */
    public static class Builder<T> {

        private ObservableSnapshotArray<T> mSnapshots;
        private LifecycleOwner mOwner;

        /**
         * Directly set the {@link ObservableSnapshotArray}.
         * <p>
         * Do not call this method after calling {@code setQuery}.
         */
        @NonNull
        public Builder<T> setSnapshotArray(@NonNull ObservableSnapshotArray<T> snapshots) {
            assertNull(mSnapshots, ERR_SNAPSHOTS_SET);

            mSnapshots = snapshots;
            return this;
        }

        /**
         * Set the query to use (with options) and provide a custom {@link SnapshotParser}.
         * <p>
         * Do not call this method after calling {@link #setSnapshotArray(ObservableSnapshotArray)}.
         */
        @NonNull
        public Builder<T> setQuery(@NonNull Query query,
                                   @NonNull QueryListenOptions options,
                                   @NonNull SnapshotParser<T> parser) {
            assertNull(mSnapshots, ERR_SNAPSHOTS_SET);

            mSnapshots = new FirestoreArray<>(query, options, parser);
            return this;
        }


        /**
         * Calls {@link #setQuery(Query, QueryListenOptions, Class)} with the default {@link
         * QueryListenOptions}.
         */
        @NonNull
        public Builder<T> setQuery(@NonNull Query query, @NonNull SnapshotParser<T> parser) {
            return setQuery(query, new QueryListenOptions(), parser);
        }

        /**
         * Set the query to use (with options) and provide a model class to which each snapshot will
         * be converted.
         * <p>
         * Do not call this method after calling {@link #setSnapshotArray(ObservableSnapshotArray)}.
         */
        @NonNull
        public Builder<T> setQuery(@NonNull Query query,
                                   @NonNull QueryListenOptions options,
                                   @NonNull Class<T> modelClass) {
            return setQuery(query, options, new ClassSnapshotParser<>(modelClass));
        }

        /**
         * Calls {@link #setQuery(Query, QueryListenOptions, Class)} with the default {@link
         * QueryListenOptions}.
         */
        @NonNull
        public Builder<T> setQuery(@NonNull Query query, @NonNull Class<T> modelClass) {
            return setQuery(query, new QueryListenOptions(), modelClass);
        }

        /**
         * Set a {@link LifecycleOwner} for the adapter. Listening will stop/start after the
         * appropriate lifecycle events.
         */
        @NonNull
        public Builder<T> setLifecycleOwner(@Nullable LifecycleOwner owner) {
            mOwner = owner;
            return this;
        }

        /**
         * Build a {@link FirestoreRecyclerOptions} from the provided arguments.
         */
        @NonNull
        public FirestoreRecyclerOptions<T> build() {
            assertNonNull(mSnapshots, ERR_SNAPSHOTS_NULL);

            return new FirestoreRecyclerOptions<>(mSnapshots, mOwner);
        }

    }

}
