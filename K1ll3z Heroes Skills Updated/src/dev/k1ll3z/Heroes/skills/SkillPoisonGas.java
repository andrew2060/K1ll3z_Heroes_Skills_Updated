package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.PeriodicDamageEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.util.Setting;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkillPoisonGas extends ActiveSkill
{
  private String expireText;

  public SkillPoisonGas(Heroes plugin)
  {
    super(plugin, "PoisonGas");
    setDescription("Poisons enemies around you for &1 seconds");
    setUsage("/skill poisongas");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill poisongas" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.SILENCABLE, SkillType.DEBUFF });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(Setting.RADIUS.node(), Integer.valueOf(10));
    node.set(Setting.DAMAGE.node(), Integer.valueOf(10));
    node.set(Setting.DURATION.node(), Integer.valueOf(5000));
    node.set(Setting.PERIOD.node(), Integer.valueOf(2000));
    node.set("tick-damage", Integer.valueOf(1));
    node.set(Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!");
    return node;
  }

  public void init()
  {
    super.init();
    this.expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT, "%target% has recovered from the poison!").replace("%target%", "$1");
  }

  public SkillResult use(Hero hero, String[] args)
  {
    Player player = hero.getPlayer();
    int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 5000, false);
    long period = SkillConfigManager.getUseSetting(hero, this, Setting.PERIOD, 2000, true);
    int tickDamage = SkillConfigManager.getUseSetting(hero, this, "tick-damage", 1, false);
    PoisonSkillEffect pEffect = new PoisonSkillEffect(this, period, duration, tickDamage, player);
    int radius = SkillConfigManager.getUseSetting(hero, this, Setting.RADIUS, 10, false);
    List entities = hero.getPlayer().getNearbyEntities(radius, radius, radius);
    Iterator i$ = entities.iterator();

    while (i$.hasNext())
    {
      Entity n = (Entity)i$.next();
      if ((n instanceof Entity)) {
        Player p = (Player)n;
        Hero tHero = this.plugin.getCharacterManager().getHero(p);
        tHero.addEffect(pEffect);
      }
    }
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 5000, false);
    int radius = SkillConfigManager.getUseSetting(hero, this, Setting.RADIUS, 20, false);
    int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);
    return getDescription().replace("$1", duration / 1000);
  }

  public class PoisonSkillEffect extends PeriodicDamageEffect
  {
    public PoisonSkillEffect(Skill skill, long period, long duration, int tickDamage, Player applier)
    {
      super("Poison", period, duration, tickDamage, applier);
      this.types.add(EffectType.POISON);
      addMobEffect(19, (int)(duration / 1000L) * 20, 0, true);
    }

    public void apply(LivingEntity lEntity) {
      super.apply((CharacterTemplate)lEntity);
    }
    public void apply(Hero hero) {
      super.apply(hero);
    }

    public void remove(LivingEntity lEntity)
    {
      super.remove((CharacterTemplate)lEntity);
      broadcast(lEntity.getLocation(), SkillPoisonGas.this.expireText, new Object[] { Messaging.getLivingEntityName(lEntity).toLowerCase() });
    }

    public void remove(Hero hero)
    {
      super.remove(hero);
      Player player = hero.getPlayer();
      broadcast(player.getLocation(), SkillPoisonGas.this.expireText, new Object[] { player.getDisplayName() });
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillPoisonGas
 * JD-Core Version:    0.6.2
 */