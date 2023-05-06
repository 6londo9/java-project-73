package hexlet.code.repository;

import com.querydsl.core.types.dsl.SimpleExpression;
import hexlet.code.model.QTask;
import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>, QuerydslBinderCustomizer<QTask> {
    @Override
    default void customize(QuerydslBindings bindings, QTask task) {
        bindings.bind(task.taskStatus.id).first(SimpleExpression::eq);
        bindings.bind(task.executor.id).first(SimpleExpression::eq);
        bindings.bind(task.labels).first((path, value) -> path.any().in(value));
        bindings.bind(task.author.id).first(SimpleExpression::eq);
    }
}
