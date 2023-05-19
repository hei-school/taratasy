package taratasy.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Value
@AllArgsConstructor
@Builder
@DynamoDbImmutable(builder = TaratasyDynamodb.TaratasyDynamodbBuilder.class)
public class TaratasyDynamodb {
  @Getter(onMethod_ = @DynamoDbPartitionKey)
  String ownerId;

  @Getter(onMethod_ = @DynamoDbSortKey)
  String fileId;

  String name;
}
