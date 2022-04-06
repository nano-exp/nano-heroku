package nano.support.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.*;

/**
 * Simple entity query SQL generator
 *
 * @param <T> Entity class
 * @author cbdyzj
 * @see org.springframework.jdbc.core.simple.SimpleJdbcInsert
 * @see org.springframework.jdbc.core.simple.SimpleJdbcCall
 * @since 2020.9.5
 */
public class SimpleJdbcSelect<T> {

    private final Class<T> entityClass;
    private final RowMapper<T> entityRowMapper;
    private final RowMapper<Integer> countRowMapper = new SingleColumnRowMapper<>(Integer.class);

    private NamedParameterJdbcTemplate jdbcTemplate;
    private String tableName;

    private String[] whereEqualColumns = {};
    private String[] whereInColumns = {};
    private String whereClause;

    private Integer limit;
    private Integer offset;

    public SimpleJdbcSelect(@NotNull Class<T> entityClass) {
        this.entityClass = entityClass;
        this.entityRowMapper = new DataClassRowMapper<>(entityClass);
    }

    public @NotNull SimpleJdbcSelect<T> usesJdbcTemplate(@NotNull NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        return this;
    }

    public @NotNull SimpleJdbcSelect<T> withTableName(@NotNull String tableName) {
        this.tableName = tableName;
        return this;
    }

    public @NotNull SimpleJdbcSelect<T> whereEqual(String @NotNull ... whereEqualColumns) {
        this.whereEqualColumns = whereEqualColumns;
        return this;
    }

    public @NotNull SimpleJdbcSelect<T> whereIn(String @NotNull ... whereInColumns) {
        this.whereInColumns = whereInColumns;
        return this;
    }

    public @NotNull SimpleJdbcSelect<T> whereClause(@NotNull String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    public @NotNull SimpleJdbcSelect<T> limit(int limit) {
        Assert.isTrue(limit >= 0, "limit < 0");
        this.limit = limit;
        return this;
    }

    public @NotNull SimpleJdbcSelect<T> offset(int offset) {
        Assert.isTrue(offset >= 0, "offset < 0");
        this.offset = offset;
        return this;
    }

    public @NotNull List<T> query(@NotNull SqlParameterSource paramSource) {
        return this.jdbcTemplate().query(this.getSql(false), paramSource, this.entityRowMapper);
    }

    public @NotNull List<T> query(@NotNull Map<String, ?> paramMap) {
        return this.jdbcTemplate().query(this.getSql(false), paramMap, this.entityRowMapper);
    }

    public @NotNull List<T> query() {
        return this.jdbcTemplate().query(this.getSql(false), this.entityRowMapper);
    }

    public @Nullable T queryOne(@NotNull SqlParameterSource paramSource) {
        return getFirst(this.query(paramSource));
    }

    public @Nullable T queryOne(@NotNull Map<String, ?> paramMap) {
        return getFirst(this.query(paramMap));
    }

    public @Nullable T queryOne() {
        return getFirst(this.query());
    }

    public int queryCount(@NotNull SqlParameterSource paramSource) {
        var count = this.jdbcTemplate().query(this.getSql(true), paramSource, this.countRowMapper);
        return getCount(count);
    }

    public int queryCount(@NotNull Map<String, ?> paramMap) {
        var count = this.jdbcTemplate().query(this.getSql(true), paramMap, this.countRowMapper);
        return getCount(count);

    }

    public int queryCount() {
        var count = this.jdbcTemplate().query(this.getSql(true), this.countRowMapper);
        return getCount(count);
    }


    public @NotNull String getSql(boolean count) {
        var sb = new StringBuilder();
        String columns;
        if (count) {
            columns = "COUNT(*)";
        } else {
            columns = String.join(", ", getEntityColumnNames(this.entityClass));
        }
        Assert.hasText(this.tableName, "this.tableName");
        sb.append("SELECT ").append(columns).append(" FROM ").append(tableName);
        boolean firstWhereClause = true;
        if (this.whereEqualColumns.length > 0) {
            Assert.state(this.whereClause == null, "\"whereEqualColumns\" and \"WhereClause\" cannot exist at the same time");
            for (String where : this.whereEqualColumns) {
                var sourceName = propertyName(where);
                if (firstWhereClause) {
                    sb.append(" WHERE ").append(where).append(" = :").append(sourceName);
                    firstWhereClause = false;
                } else {
                    sb.append(" AND ").append(where).append(" = :").append(sourceName);
                }
            }
        }
        if (this.whereInColumns.length > 0) {
            Assert.state(this.whereClause == null, "\"WhereInColumns\" and \"WhereClause\" cannot exist at the same time");
            for (String where : this.whereInColumns) {
                var sourceName = propertyName(where);
                if (firstWhereClause) {
                    sb.append(" WHERE ").append(where).append(" IN (:").append(sourceName).append(")");
                    firstWhereClause = false;
                } else {
                    sb.append(" AND ").append(where).append(" IN (:").append(sourceName).append(")");
                }
            }
        }
        if (this.whereClause != null) {
            sb.append(" ").append(slim(this.whereClause));
        }
        // count without limit and offset
        if (!count) {
            if (this.limit != null) {
                sb.append(" LIMIT ").append(this.limit);
            }
            if (this.offset != null) {
                sb.append(" OFFSET ").append(this.offset);
            }
        }
        return sb.append(";").toString();
    }

    private @NotNull NamedParameterJdbcTemplate jdbcTemplate() {
        Assert.notNull(this.jdbcTemplate, "this.jdbcTemplate is null");
        return this.jdbcTemplate;
    }

    private static int getCount(@NotNull List<Integer> count) {
        Assert.state(count.size() == 1, "Unexpected count");
        return count.get(0);
    }

    private static <T> @Nullable T getFirst(@NotNull List<T> records) {
        if (records.size() == 0) {
            return null;
        }
        return records.get(0);
    }
}
