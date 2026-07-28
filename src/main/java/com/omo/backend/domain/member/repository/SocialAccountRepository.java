package com.omo.backend.domain.member.repository;

import com.omo.backend.domain.member.entity.SocialAccount;
import com.omo.backend.domain.member.enums.MemberProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(MemberProvider provider, String providerUserId);

    boolean existsByMemberIdAndProvider(Long memberId, MemberProvider provider);

    Optional<SocialAccount> findByMemberIdAndProvider(Long memberId, MemberProvider provider);

    long countByMemberId(Long memberId);
}
