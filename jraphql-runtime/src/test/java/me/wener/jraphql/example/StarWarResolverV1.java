package me.wener.jraphql.example;

import com.github.wenerme.wava.util.Chars;
import com.github.wenerme.wava.util.Inputs;
import com.github.wenerme.wava.util.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import jodd.bean.BeanCopy;
import jodd.bean.BeanUtil;
import lombok.Data;
import lombok.SneakyThrows;
import me.wener.jraphql.exec.FieldResolveContext;
import me.wener.jraphql.exec.FieldResolver;
import me.wener.jraphql.exec.GraphExecuteException;
import me.wener.jraphql.exec.TypeResolver;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/12
 */
public class StarWarResolverV1 implements FieldResolver, TypeResolver {

  @SneakyThrows
  public static StarWarData loadData() {
    return JSON.parse(Inputs.getResourceAsString("starwar.json"), StarWarData.class);
  }

  @Override
  public Object resolve(FieldResolveContext ctx) {
    switch (ctx.getObjectName()) {
      case "Query":
        return resolveQuery(ctx);
      case "Mutation":
        switch (ctx.getFieldName()) {
          case "createReview":
            {
              StarWarData data = ctx.getRootSource();
              CreateReviewInput createReviewInput = new CreateReviewInput();
              ReviewInput input = new ReviewInput();
              BeanCopy.fromMap(ctx.getArguments()).to(createReviewInput).copy();
              BeanCopy.from(ctx.getArguments().get("review")).to(input).copy();

              createReviewInput.setReview(input);
              return data.createReview(createReviewInput);
            }
          default:
            throw new AssertionError();
        }
      case "Starship":
        return resolveStarship(ctx);
      case "Human":
        return resolveHuman(ctx);
      case "Droid":
        return resolveDroid(ctx);
      case "FriendsEdge":
        return resolveFriendsEdge(ctx);
      default:
        return BeanUtil.declared.getProperty(ctx.getSource(), ctx.getFieldName());
    }
  }

  private Object resolveDroid(FieldResolveContext ctx) {
    StarWarData data = ctx.getRootSource();

    Droid source;
    if (ctx.getSource() instanceof String) {
      source = data.getDroid(ctx.getSource());
    } else {
      source = ctx.getSource();
    }

    switch (ctx.getFieldName()) {
      case "friendsConnection":
        ConnectionArgument argument = new ConnectionArgument();
        BeanCopy.fromMap(ctx.getArguments()).toBean(argument).copy();
        return new FriendsConnection().setArgument(argument).setIds(source.getFriends()).init();
      default:
        return BeanUtil.declared.getProperty(source, ctx.getFieldName());
    }
  }

  private Object resolveHuman(FieldResolveContext ctx) {
    StarWarData data = ctx.getRootSource();

    Human source;
    if (ctx.getSource() instanceof String) {
      source = data.getHuman(ctx.getSource());
    } else {
      source = ctx.getSource();
    }

    switch (ctx.getFieldName()) {
      case "friendsConnection":
        ConnectionArgument argument = new ConnectionArgument();
        BeanCopy.fromMap(ctx.getArguments()).toBean(argument).copy();
        return new FriendsConnection().setArgument(argument).setIds(source.getFriends()).init();
      default:
        return BeanUtil.declared.getProperty(source, ctx.getFieldName());
    }
  }

  public Object resolveQuery(FieldResolveContext ctx) {
    StarWarData source = ctx.getExecuteContext().getSource();
    switch (ctx.getFieldName()) {
      case "character":
        return source.getCharacter((String) ctx.getArguments().get("id"));
      case "starship":
        return source.getStartship((String) ctx.getArguments().get("id"));
      case "human":
        return source.getHuman((String) ctx.getArguments().get("id"));
      case "droid":
        return source.getDroid((String) ctx.getArguments().get("id"));
      case "node":
        return source.get((String) ctx.getArguments().get("id"));
      case "search":
        return source.search((String) ctx.getArguments().get("text"));
      case "hero":
        String name = "R2-D2";
        if ("EMPIRE".equals(ctx.getArguments().get("episode"))) {
          return source
              .getHumans()
              .stream()
              .filter(v -> v.getName().equals("Luke Skywalker"))
              .findFirst()
              .orElse(null);
        } else {
          return source
              .getDroids()
              .stream()
              .filter(v -> v.getName().equals("R2-D2"))
              .findFirst()
              .orElse(null);
        }
      case "reviews":
        return source.getReviews().get((String) ctx.getArguments().get("episode"));
      default:
        throw new AssertionError();
    }
  }

  public Object resolveFriendsEdge(FieldResolveContext ctx) {
    String cursor = ctx.getSource();
    switch (ctx.getFieldName()) {
      case "cursor":
        return cursor;
      case "node":
        return ctx.<StarWarData>getRootSource().getCharacter(cursor);
      default:
        throw new AssertionError();
    }
  }

  public Object resolveStarship(FieldResolveContext ctx) {
    Starship source;
    if (ctx.getSource() instanceof String) {
      source = ctx.getExecuteContext().<StarWarData>getSource().getStartship(ctx.getSource());
    } else {
      source = ctx.getSource();
    }
    switch (ctx.getFieldName()) {
      case "id":
        return source.getId();
      case "name":
        return source.getName();
      case "length":
        switch ((String) ctx.getArguments().get("unit")) {
          case "METER":
            return source.getLength();
          case "FOOT":
            return source.getLength() * 3.28084;
          default:
            throw new GraphExecuteException("invalid unit");
        }
      default:
        throw new AssertionError();
    }
  }

  @Override
  public Object resolveType(FieldResolveContext resolveContext, Object source) {
    if (source instanceof String) {
      source = resolveContext.<StarWarData>getRootSource().get((String) source);
    }
    if (source instanceof Droid) {
      return "Droid";
    }
    if (source instanceof Human) {
      return "Human";
    }
    if (source instanceof Review) {
      return "Review";
    }
    if (source instanceof Starship) {
      return "Starship";
    }
    return null;
  }

  @Data
  static class Droid {
    private String id;
    private String name;
    private List<String> friends;
    private List<String> appearsIn;
    private String primaryFunction;
  }

  @Data
  static class Starship {
    private String id;
    private String name;
    private Double length;
  }

  @Data
  static class StarWarData {
    private List<Human> humans;
    private List<Droid> droids;
    private List<Starship> starships;
    private Map<String, List<Review>> reviews = Maps.newHashMap();

    public Object get(String id) {
      Object v = getCharacter(id);
      if (v == null) {
        v = getStartship(id);
      }
      if (v == null) {
        v = getReview(id);
      }
      return v;
    }

    public Object getCharacter(String id) {
      Object v = getDroid(id);
      if (v == null) {
        v = getHuman(id);
      }
      return v;
    }

    public Starship getStartship(String id) {
      return starships.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    public Review getReview(String id) {
      return reviews
          .values()
          .stream()
          .flatMap(Collection::stream)
          .filter(v -> v.getId().equals(id))
          .findFirst()
          .orElse(null);
    }

    public Droid getDroid(String id) {
      return droids.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    public Human getHuman(String id) {
      return humans.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    public Object search(String text) {
      return Streams.concat(
              humans.stream().filter(v -> Chars.containsIgnoreCase(v.getName(), text)),
              droids.stream().filter(v -> Chars.containsIgnoreCase(v.getName(), text)),
              starships.stream().filter(v -> Chars.containsIgnoreCase(v.getName(), text)))
          .collect(Collectors.toList());
    }

    public Review createReview(CreateReviewInput input) {
      Review review = new Review();
      review.setId(UUID.randomUUID().toString());
      review
          .setStars(input.getReview().getStars())
          .setCommentary(input.getReview().getCommentary());
      reviews.computeIfAbsent(input.getEpisode(), (k) -> Lists.newArrayList()).add(review);
      return review;
    }
  }

  @Data
  static class Human implements TypeResolver {
    private String id;
    private String name;
    private List<String> friends;
    private List<String> appearsIn;
    private Double height;
    private Integer mass;
    private List<String> starships;

    /** Inline the type resolve */
    @Override
    public Object resolveType(FieldResolveContext resolveContext, Object source) {
      return "Human";
    }
  }

  @Data
  static class Review {
    private String id;
    private Integer stars;
    private String commentary;
  }

  @Data
  static class PageInfo {
    private String startCursor;
    private String endCursor;
    private Boolean hasNextPage;
  }

  @Data
  static class FriendsConnection {
    private ConnectionArgument argument;
    private List<String> ids;

    private List<String> edges;
    private Integer totalCount;
    private List<String> friends;
    private PageInfo pageInfo;

    public FriendsConnection init() {
      int start = 0;
      int page = argument.getFirst() == null ? 2 : argument.getFirst();
      int end;

      if (argument.getAfter() != null) {
        start = ids.indexOf(argument.getAfter());
        if (start < 0) {
          edges = Collections.emptyList();
          // STOP
        }
      }
      if (start >= 0) {
        end = start + page;
        if (end >= ids.size()) {
          end = ids.size() - 1;
        }
        edges = ids.subList(start, end);
      }

      totalCount = edges.size();
      // use the edges as friends
      friends = edges;
      pageInfo =
          new PageInfo()
              .setStartCursor(edges.isEmpty() ? null : edges.get(0))
              .setEndCursor(edges.isEmpty() ? null : edges.get(edges.size() - 1));

      pageInfo.setHasNextPage(
          pageInfo.getEndCursor() != null && ids.indexOf(pageInfo.getEndCursor()) < ids.size() - 1);

      return this;
    }
  }

  @Data
  static class ReviewInput {
    private Integer stars;
    private String commentary;
  }

  @Data
  static class CreateReviewInput {
    private String episode;
    private ReviewInput review;
  }

  @Data
  static class ConnectionArgument {
    private Integer first;
    private String after;
  }
}
